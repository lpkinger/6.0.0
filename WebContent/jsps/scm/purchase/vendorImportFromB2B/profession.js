Ext.ns('PROFESSION');
PROFESSION = [{
	text: "IT|通信|电子|互联网",
	menu: [{
			text: "互联网/电子商务",
			handler: this.onmenucheck
		},
		{
			text: "计算机软件",
			handler: this.onmenucheck
		},

		{
			text: "IT服务(系统/数据/维护)",
			handler: this.onmenucheck
		},
		{
			text: "电子技术/半导体/集成电路",
			handler: this.onmenucheck
		},
		{
			text: "计算机硬件",
			handler: this.onmenucheck
		},
		{
			text: "通信/电信/网络设备",
			handler: this.onmenucheck
		},
		{
			text: "通信/电信运营、增值服务",
			handler: this.onmenucheck
		},
		{
			text: "网络游戏",
			handler: this.onmenucheck
		}
	]
}, {
	text: "金融业",
	menu: [{
			text: "基金/证券/期货/投资",
			handler: this.onmenucheck
		},
		{
			text: "基金/证券/期货/投资",
			handler: this.onmenucheck
		},
		{
			text: "保险",
			handler: this.onmenucheck
		},
		{
			text: "银行",
			handler: this.onmenucheck
		},
		{
			text: "信托/担保/拍卖/典当",
			handler: this.onmenucheck
		}
	]
}, {
	text: "房地产|建筑业",
	menu: [{
		text: "房地产/建筑/建材/工程",
		menu: [{
				text: "房地产金融服务类",
				handler: this.onmenucheck
			},
			{
				text: "房地产工程施工类",
				handler: this.onmenucheck
			},
			{
				text: "房地产工程货物类",
				handler: this.onmenucheck
			}
		]
	}, {
		text: "家居/室内设计/装饰装潢",
		handler: this.onmenucheck
	}, {
		text: "物业管理/商业中心",
		handler: this.onmenucheck
	}]
}, {
	text: "商业服务",
	menu: [{
			text: "专业服务/咨询(财会/法律/人力资源等)",
			handler: this.onmenucheck
		},
		{
			text: "广告/会展/公关",
			handler: this.onmenucheck
		},
		{
			text: "中介服务",
			handler: this.onmenucheck
		},
		{
			text: "检验/认证",
			handler: this.onmenucheck
		},
		{
			text: "外包服务",
			handler: this.onmenucheck
		}
	]
}, {
	text: "贸易|批发|零售|租赁业",
	menu: [{
			text: "快速消费品（食品/饮料/烟酒/日化）",
			handler: this.onmenucheck
		},
		{
			text: "耐用消费品（服饰/纺织/皮革/家具/家电）",
			handler: this.onmenucheck
		},
		{
			text: "贸易/进出口",
			handler: this.onmenucheck
		},
		{
			text: "零售/批发",
			handler: this.onmenucheck
		},
		{
			text: "租赁服务",
			handler: this.onmenucheck
		}
	]
}, {
	text: "文体教育|工艺美术",
	menu: [{
			text: "教育/培训/院校",
			handler: this.onmenucheck
		},
		{
			text: "礼品/玩具/工艺美术/收藏品/奢侈品",
			handler: this.onmenucheck
		}
	]
}, {
	text: "生产|加工|制造",
	menu: [{
			text: "汽车/摩托车",
			handler: this.onmenucheck
		},
		{
			text: "大型设备/机电设备/重工业",
			handler: this.onmenucheck
		},
		{
			text: "加工制造（原料加工/模具）",
			handler: this.onmenucheck
		},
		{
			text: "仪器仪表及工业自动化",
			handler: this.onmenucheck
		},
		{
			text: "印刷/包装/造纸",
			handler: this.onmenucheck
		},
		{
			text: "办公用品及设备",
			handler: this.onmenucheck
		},
		{
			text: "医药/生物工程",
			handler: this.onmenucheck
		},
		{
			text: "医疗设备/器械",
			handler: this.onmenucheck
		},
		{
			text: "航空/航天研究与制造",
			handler: this.onmenucheck
		}
	]
}, {
	text: "交通|运输|物流|仓储",
	menu: [{
			text: "交通/运输",
			handler: this.onmenucheck
		},
		{
			text: "物流/仓储",
			handler: this.onmenucheck
		}
	]
}, {
	text: "服务业",
	menu: [{
			text: "餐饮",
			handler: this.onmenucheck
		},
		{
			text: "美容美发",
			handler: this.onmenucheck
		},
		{
			text: "KTV",
			handler: this.onmenucheck
		},
		{
			text: "运动健身",
			handler: this.onmenucheck
		},
		{
			text: "会所",
			handler: this.onmenucheck
		},
		{
			text: "医院",
			handler: this.onmenucheck
		}
	]
}, {
	text: "文化|传媒|娱乐|体育",
	menu: [{
			text: "媒体/出版/影视/文化传播",
			handler: this.onmenucheck
		},
		{
			text: "娱乐/体育/休闲",
			handler: this.onmenucheck
		}
	]
}, {
	text: "能源|矿产|环保",
	menu: [{
			text: "能源/矿产/采掘/冶炼",
			handler: this.onmenucheck
		},
		{
			text: "石油/石化/化工",
			handler: this.onmenucheck
		},
		{
			text: "电气/电力/水利",
			handler: this.onmenucheck
		},
		{
			text: "环保",
			handler: this.onmenucheck
		}
	]
}, {
	text: "政府|非盈利机构",
	menu: [{
			text: "政府/公共事业/非盈利机构",
			handler: this.onmenucheck
		},
		{
			text: "学术/科研",
			handler: this.onmenucheck
		}
	]
}, {
	text: "农|林|牧|渔|其他",
	menu: [{
			text: "农/林/牧/渔",
			handler: this.onmenucheck
		},
		{
			text: "跨领域经营",
			handler: this.onmenucheck
		},
		{
			text: "其他",
			handler: this.onmenucheck
		}
	]
}];