
#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
股票数据获取脚本
用于从东方财富网API获取股票代码数据并进行结构化存储
"""

import requests
import json
import csv
import os
from datetime import datetime

class StockDataFetcher:
    """股票数据获取器类"""

    def __init__(self):
        self.url = "https://78.push2.eastmoney.com/api/qt/clist/get"
        self.params = {
            "pn": "1",
            "pz": "5000",
            "po": "1",
            "np": "1",
            "ut": "bd1d9ddb04089700cf9c27f6f7426281",
            "fltt": "2",
            "invt": "2",
            "fid": "f3",
            "fs": "m:0+t:6,m:0+t:80,m:1+t:2,m:1+t:23,m:0+t:81+s:2048",
            "fields": "f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152"
        }
        # 字段映射，让数据更易读
        self.field_mapping = {
            'f1': '总市值',
            'f2': '最新价',
            'f3': '涨跌幅',
            'f4': '涨跌额',
            'f5': '换手率',
            'f6': '量比',
            'f7': '振幅',
            'f8': '成交额',
            'f9': '成交量',
            'f10': '市净率',
            'f12': '股票代码',
            'f13': '市场代码',
            'f14': '股票名称',
            'f15': '最高',
            'f16': '最低',
            'f17': '今开',
            'f18': '昨收',
            'f20': '量比',
            'f21': '换手率',
            'f23': '市净率',
            'f24': '市盈率(TTM)',
            'f25': '市盈率(静)',
            'f22': '总市值',
            'f11': '市盈率(动)',
            'f62': '昨收盘',
            'f128': '市销率',
            'f136': '市现率',
            'f115': '股息率',
            'f152': '财务更新日期'
        }

    def fetch_data(self):
        """获取股票数据"""
        try:
            print("正在从东方财富网API获取股票数据...")
            headers = {
                'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36',
                'Referer': 'http://quote.eastmoney.com/',
                'Accept': 'application/json, text/javascript, */*; q=0.01',
                'Accept-Language': 'zh-CN,zh;q=0.9',
                'Connection': 'keep-alive'
            }
            response = requests.get(self.url, params=self.params, headers=headers, timeout=30)
            # 若状态码非 2xx 则抛出异常
            response.raise_for_status()

            # 打印响应状态以供调试
            print(f"响应状态码: {response.status_code}")

            try:
                data = response.json()
                print(f"JSON解析成功")
                # 检查数据结构，更灵活地获取diff数组
                if isinstance(data, dict):
                    # 检查各种可能的数据路径
                    if 'data' in data and 'diff' in data['data']:
                        stock_list = data['data']['diff']
                        print(f"成功获取到 {len(stock_list)} 条股票数据")
                        return stock_list
                    else:
                        # 打印数据结构以便调试
                        print("数据结构:")
                        for key in data.keys():
                            print(f"- {key}: {type(data[key]).__name__}")
                        # 尝试直接在data中查找diff
                        if 'diff' in data:
                            stock_list = data['diff']
                            print(f"成功获取到 {len(stock_list)} 条股票数据")
                            return stock_list
                        else:
                            print("未找到'diff'字段")
                            # 返回空列表而不是None，以便程序可以继续
                            return []
                else:
                    print(f"返回数据不是字典类型，而是: {type(data).__name__}")
                    return []

            except json.JSONDecodeError as json_err:
                print(f"JSON解析错误: {str(json_err)}")
                # 截取展示前200个字符
                print(f"原始响应内容: {response.text[:200]}...")
                return []

        except requests.exceptions.RequestException as e:
            print(f"HTTP请求错误: {str(e)}")
            if 'response' in locals():
                print(f"响应内容: {response.text[:200] if response.content else '无内容'}...")
            return []
        except Exception as e:
            print(f"请求API时发生未知错误: {str(e)}")
            import traceback
            traceback.print_exc()
            return []

    def convert_to_readable_format(self, raw_data):
        """将原始数据转换为可读性更好的格式"""
        if not raw_data:
            return []

        formatted_data = []
        for item in raw_data:
            formatted_item = {}
            for key, value in item.items():
                # 使用映射的中文名称作为键
                new_key = self.field_mapping.get(key, key)
                formatted_item[new_key] = value
            formatted_data.append(formatted_item)

        return formatted_data

    def save_as_json(self, data, filename=None):
        """将数据保存为JSON文件"""
        if not data:
            return False

        if not filename:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = f"stock_data_{timestamp}.json"

        try:
            with open(filename, 'w', encoding='utf-8') as f:
                json.dump(data, f, ensure_ascii=False, indent=2)
            print(f"数据已保存到JSON文件: {filename}")
            return True
        except Exception as e:
            print(f"保存JSON文件时发生错误: {str(e)}")
            return False

    def save_as_csv(self, data, filename=None):
        """将数据保存为CSV文件"""
        if not data:
            return False

        if not filename:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = f"stock_data_{timestamp}.csv"

        try:
            # 获取所有可能的字段名
            fieldnames = set()
            for item in data:
                fieldnames.update(item.keys())
            fieldnames = sorted(list(fieldnames))

            with open(filename, 'w', newline='', encoding='utf-8-sig') as f:
                writer = csv.DictWriter(f, fieldnames=fieldnames)
                writer.writeheader()
                writer.writerows(data)

            print(f"数据已保存到CSV文件: {filename}")
            return True
        except Exception as e:
            print(f"保存CSV文件时发生错误: {str(e)}")
            return False

    def main(self):
        """主函数"""
        # 创建output目录
        output_dir = "output"
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)

        # 获取数据
        raw_data = self.fetch_data()

        if not raw_data or len(raw_data) == 0:
            print("获取到的数据为空，程序退出")
            return

        print(f"开始处理 {len(raw_data)} 条股票数据...")

        # 转换为可读格式
        formatted_data = self.convert_to_readable_format(raw_data)

        # 保存数据
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        json_file = os.path.join(output_dir, f"stock_data_{timestamp}.json")
        csv_file = os.path.join(output_dir, f"stock_data_{timestamp}.csv")

        saved_json = self.save_as_json(formatted_data, json_file)
        saved_csv = self.save_as_csv(formatted_data, csv_file)

        if saved_json or saved_csv:
            print("\n数据保存成功！")

        # 显示前几条数据作为示例
        print("\n数据示例:")
        for i, item in enumerate(formatted_data[:3]):
            print(f"\n股票 {i+1}:")
            # 显示几个关键字段
            keys_to_show = ['股票代码', '股票名称', '最新价', '涨跌幅', '总市值']
            for key in keys_to_show:
                if key in item:
                    print(f"  {key}: {item[key]}")

        print(f"\n总共有 {len(formatted_data)} 条股票数据被处理")
        print(f"JSON文件保存路径: {json_file}")
        print(f"CSV文件保存路径: {csv_file}")

if __name__ == "__main__":
    fetcher = StockDataFetcher()
    fetcher.main()
