import pymongo

# 连接MongoDB数据库
client = pymongo.MongoClient("mongodb://localhost:27017/")  # 默认端口为27017，如果不是，请相应更改

# 创建一个数据库（如果不存在则创建）
mydb = client["mydatabase"]

# 创建一个集合（如果不存在则创建）
mycol = mydb["customers"]

# 插入一条数据
mydict = { "name": "John", "address": "Highway 37" }
x = mycol.insert_one(mydict)

# 打印插入的数据的ID
print(x.inserted_id)

# 查询所有数据
for x in mycol.find():
  print(x)

# 查询特定条件的数据
myquery = { "address": "Highway 37" }
mydoc = mycol.find(myquery)
for x in mydoc:
  print(x)