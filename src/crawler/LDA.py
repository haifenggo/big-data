import pandas as pd
from gensim import corpora
from gensim.models import LdaModel
from gensim.parsing.preprocessing import preprocess_string
from gensim.parsing.preprocessing import remove_stopwords
from trans import *
from collections import Counter
from pyecharts import options as opts
from pyecharts.charts import WordCloud
from pyecharts.globals import SymbolType
import wordcloud #词云展示库
from PIL import Image,ImageDraw,ImageFont#图像处理库
import matplotlib.pyplot as plt

def preprocess_text(text):
    # 分词和去除停用词
    custom_filters = [lambda x: x.lower(), remove_stopwords]
    tokens = preprocess_string(text, filters=custom_filters)
    return tokens

def lda_topic_modeling(danmu_data, num_topics=3, num_words=10):
    # 创建词典和文档-词频矩阵
    dictionary = corpora.Dictionary(danmu_data)
    corpus = [dictionary.doc2bow(tokens) for tokens in danmu_data]
    
    # 运行LDA模型
    lda_model = LdaModel(corpus, num_topics=num_topics, id2word=dictionary, passes=15)
    
    # 提取主题和对应的词语
    topics = []
    for topic_id in range(num_topics):
        topic_words = lda_model.show_topic(topic_id, num_words)
        topics.append([{word : str(_)} for word, _ in topic_words])
    return topics

def word_frequency_analysis(texts, top_n=10):
    # 将所有文本合并为一个长字符串
    all_text = ''
    for text in texts:
        for word in text:
            all_text = all_text + ' ' + word
    
    # 分词
    words = all_text.split()
    
    # 计算词频
    word_counts = Counter(words)
    
    # 获取词频最高的前top_n个词语和对应的频率
    top_words = word_counts.most_common(top_n)
    ret = []
    for tup in top_words:
        ret.append(top_words[0])
    return top_words

def wordCloud(words):
    # mask= plt.imread('彩色浪花3.jpg') #这个jpg文件就是你想要展示词云的背景图片
    wc=wordcloud.WordCloud(
        scale=20,
        background_color='white',
        font_path='./files/SimHei.ttf', #设置字体格式以显示中文
        # mask=mask,
        max_words=200, #设置所展示词组数量的最大值
        max_font_size=100, #设置字体的最大值
        random_state=30
    )
    wc.generate_from_frequencies(words) #从字典生成词云
    # image_colors=wordcloud.ImageColorGenerator(mask) #获取背景图颜色信息
    # wc.recolor(color_func=image_colors) #将词云颜色设置为背景图颜色
    plt.imshow(wc,interpolation='bilinear') #显示词云
    plt.axis('off') #关闭坐标轴
    wc.to_file('../main/resources/data/wordcloud.png')
    plt.show()
    


if __name__ == '__main__':
    # 从CSV文件中读取弹幕数据
    danmu_data = pd.read_csv("./tmp2.csv")  # 请根据实际情况替换文件路径
    danmu_data = startTrans(danmu_data['content'])
    # 运行主题建模
    topics = lda_topic_modeling(danmu_data)
    print(topics)
    # print(danmu_data)
    # hot = word_frequency_analysis(danmu_data)
    # print(hot)