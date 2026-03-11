大模型的局限性： 
知识的局限性，无法访问内部资料 FAQ QA
知识的实时性， 训练模型的实时性
幻觉问题，


RAG 技术 检索增强生成
先去检索相关的文档(向量数据库中查找文档)，然后再生成回答。
问题转换成向量，然后去向量数据库中查找相关的文档。
把问题转换成向量，语义相似度来查找，避免全文搜索匹配
提问前 分片 - 索引
提问后 召回 重拍 生成

naive rag 
文档 chunking 文档分片 - embedding 生成向量 - vector store -

advanced rag 
改写 增强 生成

modular rag 

search memory rerank database module 模块
agent 智能体

langchain Llamaindex 编排框架
chroma faiss   向量数据库
openai  bge cieembedding 模型 文本变成向量
gpt qwen claude 大语言模型

找到信息 -> 找的准 -> 怎么找自主规划任务的智能助理

分片 索引 
召回 重排 生成


deepseek 大模型
所涉及的模型有 DeepSeek-V3.2

智谱AI大模型
所涉及的模型有 glm-5

siliconFlow 大模型
所涉及的大模型 Pro/MiniMaxAI/MiniMax-M2.5

kimi 大模型 月之暗面
所涉及的大模型 kimi-k2.5



