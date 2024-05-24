import pandas as pd
from collections import Counter
import matplotlib.pyplot as plt

def word_frequency_analysis(texts, top_n=10):
    # 将所有文本合并为一个长字符串
    all_text = ' '.join(texts)
    
    # 分词
    words = all_text.split()
    
    # 计算词频
    word_counts = Counter(words)
    
    # 获取词频最高的前top_n个词语和对应的频率
    top_words = word_counts.most_common(top_n)
    
    return top_words

# 示例文本数据
texts = [
    "这个视频真棒！",
    "up主讲解得非常清晰",
    "画质有点模糊",
    "不错的内容，值得推荐",
    "期待下一期视频",
    "感谢up主的辛苦付出"
]

# 运行词频分析
top_words = word_frequency_analysis(texts)

# 打印词频最高的前10个词语和对应的频率
print("词语\t\t频率")
for word, count in top_words:
    print(f"{word}\t\t{count}")

# 可视化词频分析结果
df = pd.DataFrame(top_words, columns=['词语', '频率'])
plt.figure(figsize=(10, 6))
plt.bar(df['词语'], df['频率'], color='skyblue')
plt.xlabel('词语')
plt.ylabel('频率')
plt.title('词频分析结果')
plt.xticks(rotation=45)
plt.tight_layout()
plt.show()
