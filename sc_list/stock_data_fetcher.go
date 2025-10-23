package main

import (
	"encoding/csv"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"os"
	"time"
)

// FieldMapping 字段映射表
var FieldMapping = map[string]string{
	"f1":  "总市值",
	"f2":  "最新价",
	"f3":  "涨跌幅",
	"f4":  "涨跌额",
	"f5":  "换手率",
	"f6":  "量比",
	"f7":  "振幅",
	"f8":  "成交额",
	"f9":  "成交量",
	"f10": "市净率",
	"f12": "股票代码",
	"f13": "市场代码",
	"f14": "股票名称",
	"f15": "最高",
	"f16": "最低",
	"f17": "今开",
	"f18": "昨收",
	"f20": "量比",
	"f21": "换手率",
	"f23": "市净率",
	"f24": "市盈率(TTM)",
	"f25": "市盈率(静)",
	"f22": "总市值",
	"f11": "市盈率(动)",
	"f62": "昨收盘",
	"f128": "市销率",
	"f136": "市现率",
	"f115": "股息率",
	"f152": "财务更新日期",
}

// ResponseData API响应数据结构
type ResponseData struct {
	Code int         `json:"code"`
	Msg  string      `json:"msg"`
	Data *InnerData  `json:"data,omitempty"`
}

// InnerData 内部数据结构
type InnerData struct {
	Diff []map[string]interface{} `json:"diff"`
}

// StockDataFetcher 股票数据获取器
type StockDataFetcher struct {
	URL    string
	Params map[string]string
}

// NewStockDataFetcher 创建新的股票数据获取器
func NewStockDataFetcher() *StockDataFetcher {
	return &StockDataFetcher{
		URL: "https://78.push2.eastmoney.com/api/qt/clist/get",
		Params: map[string]string{
			"pn":   "1",
			"pz":   "5000",
			"po":   "1",
			"np":   "1",
			"ut":   "bd1d9ddb04089700cf9c27f6f7426281",
			"fltt": "2",
			"invt": "2",
			"fid":  "f3",
			"fs":   "m:0+t:6,m:0+t:80,m:1+t:2,m:1+t:23,m:0+t:81+s:2048",
			"fields": "f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152",
		},
	}
}

// FetchData 获取股票数据
func (f *StockDataFetcher) FetchData() ([]map[string]interface{}, error) {
	fmt.Println("正在从东方财富网API获取股票数据...")

	// 创建HTTP客户端
	client := &http.Client{
		Timeout: 30 * time.Second,
	}

	// 构建请求URL
	req, err := http.NewRequest("GET", f.URL, nil)
	if err != nil {
		return nil, fmt.Errorf("创建请求失败: %v", err)
	}

	// 设置查询参数
	query := req.URL.Query()
	for k, v := range f.Params {
		query.Add(k, v)
	}
	req.URL.RawQuery = query.Encode()

	// 设置请求头
	req.Header.Set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36")
	req.Header.Set("Referer", "http://quote.eastmoney.com/")
	req.Header.Set("Accept", "application/json, text/javascript, */*; q=0.01")
	req.Header.Set("Accept-Language", "zh-CN,zh;q=0.9")
	req.Header.Set("Connection", "keep-alive")

	// 发送请求
	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("发送请求失败: %v", err)
	}
	defer resp.Body.Close()

	// 检查响应状态码
	fmt.Printf("响应状态码: %d\n", resp.StatusCode)
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("HTTP请求错误，状态码: %d", resp.StatusCode)
	}

	// 读取响应体
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("读取响应体失败: %v", err)
	}

	// 解析JSON
	var responseData ResponseData
	err = json.Unmarshal(body, &responseData)
	if err != nil {
		// 尝试直接解析diff字段
		var altData map[string]interface{}
		if altErr := json.Unmarshal(body, &altData); altErr == nil {
			fmt.Println("JSON解析成功")

			// 检查是否有data.diff
			if data, ok := altData["data"].(map[string]interface{}); ok {
				if diff, ok := data["diff"].([]interface{}); ok {
					return f.convertDiffToMap(diff), nil
				}
			}

			// 检查是否直接有diff
			if diff, ok := altData["diff"].([]interface{}); ok {
				return f.convertDiffToMap(diff), nil
			}

			return nil, fmt.Errorf("未找到股票数据")
		}
		return nil, fmt.Errorf("JSON解析错误: %v", err)
	}

	fmt.Println("JSON解析成功")

	// 检查响应状态
	if responseData.Code == 0 && responseData.Data != nil && len(responseData.Data.Diff) > 0 {
		fmt.Printf("成功获取到 %d 条股票数据\n", len(responseData.Data.Diff))
		return responseData.Data.Diff, nil
	}

	return nil, fmt.Errorf("获取数据失败: %s", responseData.Msg)
}

// convertDiffToMap 转换diff数组为map数组
func (f *StockDataFetcher) convertDiffToMap(diff []interface{}) []map[string]interface{} {
	result := make([]map[string]interface{}, 0, len(diff))
	for _, item := range diff {
		if m, ok := item.(map[string]interface{}); ok {
			result = append(result, m)
		}
	}
	fmt.Printf("成功获取到 %d 条股票数据\n", len(result))
	return result
}

// ConvertToReadableFormat 转换为可读格式
func (f *StockDataFetcher) ConvertToReadableFormat(rawData []map[string]interface{}) []map[string]interface{} {
	formattedData := make([]map[string]interface{}, 0, len(rawData))
	for _, item := range rawData {
		formattedItem := make(map[string]interface{})
		for key, value := range item {
			if newKey, ok := FieldMapping[key]; ok {
				formattedItem[newKey] = value
			} else {
				formattedItem[key] = value
			}
		}
		formattedData = append(formattedData, formattedItem)
	}
	return formattedData
}

// SaveAsJSON 保存为JSON文件
func (f *StockDataFetcher) SaveAsJSON(data []map[string]interface{}, filename string) error {
	jsonData, err := json.MarshalIndent(data, "", "  ")
	if err != nil {
		return fmt.Errorf("JSON序列化失败: %v", err)
	}

	err = ioutil.WriteFile(filename, jsonData, 0644)
	if err != nil {
		return fmt.Errorf("写入JSON文件失败: %v", err)
	}

	fmt.Printf("数据已保存到JSON文件: %s\n", filename)
	return nil
}

// SaveAsCSV 保存为CSV文件
func (f *StockDataFetcher) SaveAsCSV(data []map[string]interface{}, filename string) error {
	if len(data) == 0 {
		return fmt.Errorf("数据为空，无法保存CSV")
	}

	// 获取所有字段名
	fieldnames := make(map[string]bool)
	for _, item := range data {
		for key := range item {
			fieldnames[key] = true
		}
	}

	// 转换为切片
	fields := make([]string, 0, len(fieldnames))
	for field := range fieldnames {
		fields = append(fields, field)
	}

	// 创建文件
	file, err := os.Create(filename)
	if err != nil {
		return fmt.Errorf("创建CSV文件失败: %v", err)
	}
	defer file.Close()

	// 创建CSV写入器
	writer := csv.NewWriter(file)
	defer writer.Flush()

	// 写入表头
	err = writer.Write(fields)
	if err != nil {
		return fmt.Errorf("写入CSV表头失败: %v", err)
	}

	// 写入数据
	for _, item := range data {
		row := make([]string, len(fields))
		for i, field := range fields {
			if value, ok := item[field]; ok {
				row[i] = fmt.Sprintf("%v", value)
			}
		}
		err = writer.Write(row)
		if err != nil {
			return fmt.Errorf("写入CSV数据失败: %v", err)
		}
	}

	fmt.Printf("数据已保存到CSV文件: %s\n", filename)
	return nil
}

// EnsureDir 确保目录存在
func EnsureDir(dir string) error {
	if _, err := os.Stat(dir); os.IsNotExist(err) {
		return os.MkdirAll(dir, 0755)
	}
	return nil
}

func main() {
	// 创建输出目录
	outputDir := "output"
	err := EnsureDir(outputDir)
	if err != nil {
		fmt.Printf("创建输出目录失败: %v\n", err)
		return
	}

	// 创建获取器
	fetcher := NewStockDataFetcher()

	// 获取数据
	rawData, err := fetcher.FetchData()
	if err != nil {
		fmt.Printf("获取数据失败: %v\n", err)
		return
	}

	if len(rawData) == 0 {
		fmt.Println("获取到的数据为空，程序退出")
		return
	}

	fmt.Printf("开始处理 %d 条股票数据...\n", len(rawData))

	// 转换为可读格式
	formattedData := fetcher.ConvertToReadableFormat(rawData)

	// 生成时间戳
	timestamp := time.Now().Format("20060102_150405")
	jsonFile := fmt.Sprintf("%s/stock_data_go_%s.json", outputDir, timestamp)
	csvFile := fmt.Sprintf("%s/stock_data_go_%s.csv", outputDir, timestamp)

	// 保存数据
	savedJSON := true
	if err := fetcher.SaveAsJSON(formattedData, jsonFile); err != nil {
		fmt.Printf("保存JSON失败: %v\n", err)
		savedJSON = false
	}

	savedCSV := true
	if err := fetcher.SaveAsCSV(formattedData, csvFile); err != nil {
		fmt.Printf("保存CSV失败: %v\n", err)
		savedCSV = false
	}

	if savedJSON || savedCSV {
		fmt.Println("\n数据保存成功！")
	}

	// 显示前几条数据
	fmt.Println("\n数据示例:")
	showCount := 3
	if len(formattedData) < showCount {
		showCount = len(formattedData)
	}

	keysToShow := []string{"股票代码", "股票名称", "最新价", "涨跌幅", "总市值"}
	for i := 0; i < showCount; i++ {
		fmt.Printf("\n股票 %d:\n", i+1)
		for _, key := range keysToShow {
			if value, ok := formattedData[i][key]; ok {
				fmt.Printf("  %s: %v\n", key, value)
			}
		}
	}

	fmt.Printf("\n总共有 %d 条股票数据被处理\n", len(formattedData))
	fmt.Printf("JSON文件保存路径: %s\n", jsonFile)
	fmt.Printf("CSV文件保存路径: %s\n", csvFile)
}
