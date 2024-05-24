import jieba #中文分词
import jieba.analyse
import codecs #用于编码转换
from langconv import *
import jieba.posseg as pseg
import sys
#添加jieba无法识别的词组，tag用来定义词性
jieba.add_word('哔哩哔哩',tag='nt')
jieba.add_word('老番茄',tag='nr')
jieba.add_word('B站',tag='nt')
stoplist=[i.strip() for i in codecs.open('./files/stop_words.txt', 'r', 'utf-8').readlines()]

def cht_to_chs(line):
    line = Converter('zh-hans').convert(line)
    line.encode('utf-8')
    return line

def str_convertU(content):
    strs=[]
    for each_char in content:
        code_num = ord(each_char) 
        if code_num==12288:
            code_num=32
        elif 65281<=code_num<=65374:
            code_num-=65248
        strs.append(chr(code_num))
    return ''.join(strs)

def jieba_cut(text):
    words=pseg.cut(text)
    rule_word=['z','vn','v','t','nz','nr','ns','n','nt','l','i','j','an','a','eng']
    seg_list=[word.word.upper() for word in words if word.flag in rule_word]
    cov_list=[cht_to_chs(word) for word in seg_list if word not in stoplist]
    return cov_list

def get_word_list(all_data):
    words_list=[jieba_cut(each_content) for each_content in all_data]
    return words_list

def get_words(all_data):
    words = []
    for line in all_data:
        for word in line:
            words.append(word)
    return words