Ext.define('erp.view.sys.base.DefaultPanel',{    
	extend: 'Ext.panel.Panel', 
	alias: 'widget.defaultpanel',
	caller:null,
	flag:'',
	layout: 'border',
	type:'',
	initComponent : function(){
		var me=this;
		me.getItmes();
		this.callParent(arguments);
	},
	configure:[
	           {flag:'Sale-ForecastKind',table:'SALEFORECASTKIND',keyField:'SF_ID',codeField:'SF_CODE',caller:'SaleForecastKind!saas',statusfield:'SF_STATUS',statuscodefield:'SF_STATUSCODE',
				fields:'SF_NAME,SF_CLASHOPTION,SF_MRP,SF_IFNOTTHROWMAKE,SF_IFSHORTLTNOTTHROW,SF_STATUS,SF_STATUSCODE,SF_SHORTLTDAYS,SF_MRPLEADDAY,SF_ID,SF_CODE',
				relfields:"SF_NAME",seq:'SALEFORECASTKIND_SEQ',
				columns:[{dataIndex:'SF_ID',width: 0,text:'ID'},{dataIndex:'SF_CODE',width: 0,text:'销售预测类型编号'},
					     {dataIndex:'SF_NAME',width: 120,text:'&nbsp&nbsp销售预测类型名称',logic: 'necessaryField',
							editor:{xtype:'textfield',field:'SF_NAME'}},
						{dataIndex:'SF_MRP',width: 60,text:'参与MRP',xtype:'checkcolumn'},
						 {dataIndex:'SF_CLASHOPTION',width: 80,text:'&nbsp&nbsp冲销选项',xtype:'combocolumn',logic: 'necessaryField',
						 	filter:{xtype: 'combo',queryMode: 'local',filterName:'SF_CLASHOPTION',
		    						displayField: 'display',valueField: 'value',
		    						store:{fields: ['display', 'value'],
		    							   data:[{display: "订单冲销", value: "SALE"},{display: "完工冲销", value: "FINISH"}, {display: "发货冲销", value: "SEND"}]
		    						}
		    						},
		    				editor:{
										xtype:'combo',
										field:'SF_CLASHOPTION',
										queryMode: 'local',
							    		displayField: 'display',
							    		valueField: 'value',
							    		editable:false,
										store:{fields: ["display", "value"],
											  data:[{display: "订单冲销", value: "SALE"},{display: "完工冲销", value: "FINISH"}, {display: "发货冲销", value: "SEND"}]
				    					}
							},renderer:function(val){
	    						if(val != null && val.toString().trim() != ''){
	    							switch (val) {
						    					case 'SALE'		:rVal = "订单冲销"; break;
						                        case 'FINISH'	:rVal = "完工冲销";  break;
						                        case 'SEND'	:rVal = "发货冲销";  break;
				                    }
				    				return rVal;
	    						} else {
	    							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
	    							'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
	    						}
	    					}},
		    			{dataIndex:'SF_IFNOTTHROWMAKE',width: 90,text:'不投放工单',xtype:'checkcolumn'},
		    			{dataIndex:'SF_IFSHORTLTNOTTHROW',width: 120,text:'短纳期不备料',xtype:'checkcolumn'},
		    			{dataIndex:'SF_SHORTLTDAYS',width: 80,text:'短纳期天数',xtype:'numbercolumn',editor:{xtype:'numberfield',field:'SF_SHORTLTDAYS',hideTrigger:true}},
		    			{dataIndex:'SF_MRPLEADDAY',width: 80,text:'备货提前天数',xtype:'numbercolumn',editor:{xtype:'numberfield',field:'SF_MRPLEADDAY'}},
		    			{dataIndex:'SF_STATUS',width: 0,text:'状态',editor:{xtype:'textfield',field:'SF_STATUS'},value:'已审核'},
		    			{dataIndex:'SF_STATUSCODE',width: 0,text:'状态码',editor:{xtype:'textfield',field:'SF_STATUSCODE'},value:'AUDITED'}
		    	],
		    	gridCondition:'1=1',
				saveUrl: 'scm/sale/saveSaleForecastKind.action',
				deleteUrl: 'scm/sale/deleteSaleForecastKind.action',
				updateUrl: 'scm/sale/updateSaleForecastKind.action',
				getIdUrl: 'common/getId.action?seq=SALEFORECASTKIND_SEQ'
				 },
			   {flag:'Sale-Kind',table:'SaleKind',keyField:'SK_ID',codeField:'SK_CODE',caller:'SaleKind!saas',statusfield:'SK_STATUS',statuscodefield:'SK_STATUSCODE',
				fields:'SK_NOBOMLEVEL,SK_IFB2C,SK_ALLOWZERO,SK_NAME,SK_STATUS,SK_STATUSCODE,SK_MRP,SK_OUTTYPE,SK_ISSAMPLE,SK_CLASHOPTION,SK_CLASHFOR,SK_CLASHKIND,SK_ISSALEPRICE,SK_PRICEKIND,SK_CODE,SK_ID',
				relfields:"SK_NAME",seq:'SaleKind_SEQ',
				columns:[{dataIndex:'SK_ID',width: 0,text:'ID'},{dataIndex:'SK_CODE',width: 0,text:'销售类型编号'},
					     {dataIndex:'SK_NAME',width: 85,text:'销售类型名称',logic: 'necessaryField',editor:{xtype:'textfield',field:'SK_NAME'}},
					     {dataIndex:'SK_MRP',width: 60,text:'参与MRP',xtype:'checkcolumn'},
					     {dataIndex:'SK_NOBOMLEVEL',width: 0,text:'不限BOM状态',xtype:'checkcolumn'},
					     {dataIndex:'SK_IFB2C',width: 0,text:'优软商城订单',xtype:'checkcolumn'},
					     {dataIndex:'SK_PRICEKIND',width: 150,text:'取价原则',xtype:'combocolumn',logic: 'necessaryField',
							 	filter:{xtype: 'combo',queryMode: 'local',filterName:'SK_PRICEKIND',
			    						displayField: 'display',valueField: 'value',
			    						store:{fields: ['display', 'value'],
			    							   data:[{display: "不取价", value: "NG"},{display: "客户+币别+料号", value: "CCP"}, {display: "取价类型+币别+料号", value: "KCP"}, {display: "料号+币别", value: "PC"}, {display: "销售类型+币别+料号", value: "SCP"}, {display: "客户+币别+料号+税率", value: "CCPR"}]
			    						}
			    			},
			    			editor:{
								xtype:'combo',
								field:'SK_PRICEKIND',
								queryMode: 'local',
					    		displayField: 'display',
					    		valueField: 'value',
					    		editable:false,
								store:{fields: ["display", "value"],
									data:[{display: "不取价", value: "NG"},{display: "客户+币别+料号", value: "CCP"}, {display: "取价类型+币别+料号", value: "KCP"}, {display: "料号+币别", value: "PC"}, {display: "销售类型+币别+料号", value: "SCP"}, {display: "客户+币别+料号+税率", value: "CCPR"}]
	    						}
							},renderer:function(val){
	    						if(val != null && val.toString().trim() != ''){
	    							switch (val) {
						    					case 'NG'		:rVal = "不取价"; break;
						                        case 'CCP'	:rVal = "客户+币别+料号";  break;
						                        case 'KCP'	:rVal = "取价类型+币别+料号";  break;
						                        case 'PC'		:rVal = "料号+币别"; break;
						                        case 'SCP'	:rVal = "销售类型+币别+料号";  break;
						                        case 'CCPR'	:rVal = "客户+币别+料号+税率";  break;
				                    }
				    				return rVal;
	    						} else {
	    							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
	    							'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
	    						}
	    					}},
			    		{dataIndex:'SK_ALLOWZERO',width: 80,text:'允许0单价',xtype:'checkcolumn'},
			    		{dataIndex:'SK_ISSALEPRICE',width: 40,text:'定价',xtype:'checkcolumn'},
			    		{dataIndex:'SK_CLASHOPTION',width: 100,text:'冲销触发类型',xtype:'combocolumn',logic: 'necessaryField',
						 	filter:{xtype: 'combo',queryMode: 'local',filterName:'SK_CLASHOPTION',
		    						displayField: 'display',valueField: 'value',
		    						store:{fields: ['display', 'value'],
		    							   data:[{display: "订单冲销", value: "订单冲销"},{display: "发货冲销", value: "发货冲销"}, {display: "不冲销", value: "不冲销"}]
		    						}
		    						},
		    						editor:{
										xtype:'combo',
										field:'SK_CLASHOPTION',
										queryMode: 'local',
							    		displayField: 'display',
							    		valueField: 'value',
							    		editable:false,
							    		store:{fields: ['display', 'value'],
			    							   data:[{display: "订单冲销", value: "订单冲销"},{display: "发货冲销", value: "发货冲销"}, {display: "不冲销", value: "不冲销"}]
			    						}
									}},
			    		{dataIndex:'SK_CLASHFOR',width: 100,text:'冲销匹配原则',xtype:'combocolumn',logic: 'necessaryField',
						 	filter:{xtype: 'combo',queryMode: 'local',filterName:'SK_CLASHFOR',
		    						displayField: 'display',valueField: 'value',
		    						store:{fields: ['display', 'value'],
		    									data:[{display: "单号冲销", value: "单号冲销"},{display: "无", value: "无"}, {display: "料号冲销", value: "料号冲销"}]
		    								}
		    						},
		    						editor:{
										xtype:'combo',
										field:'SK_CLASHFOR',
										queryMode: 'local',
							    		displayField: 'display',
							    		valueField: 'value',
							    		editable:false,
							    		store:{fields: ['display', 'value'],
			    							   data:[{display: "单号冲销", value: "单号冲销"},{display: "无", value: "无"}, {display: "料号冲销", value: "料号冲销"}]
			    						}
									}},
						{dataIndex:'SK_CLASHKIND',width: 100,text:'附加冲销条件',xtype:'combocolumn',logic: 'necessaryField',
						 	filter:{xtype: 'combo',queryMode: 'local',filterName:'SK_CLASHKIND',
		    						displayField: 'display',valueField: 'value',
		    						store:{fields: ['display', 'value'],
		    							   data:[{display: "客户匹配", value: "客户匹配"},{display: "无", value: "无"}]
		    						}
		    						},editor:{
										xtype:'combo',
										field:'SK_CLASHKIND',
										queryMode: 'local',
							    		displayField: 'display',
							    		valueField: 'value',
							    		editable:false,
							    		store:{fields: ['display', 'value'],
			    							   data:[{display: "客户匹配", value: "客户匹配"},{display: "无", value: "无"}]
			    						}
									}},
						{dataIndex:'SK_OUTTYPE',width: 100,text:'出货类型',xtype:'combocolumn',logic: 'necessaryField',
						 	filter:{xtype: 'combo',queryMode: 'local',filterName:'SK_OUTTYPE',
		    						displayField: 'display',valueField: 'value',
		    						store:{fields: ['display', 'value'],
		    							   data:[{display: "转出货通知单", value: "TURNSN"},{display: "转出货单", value: "TURNOUT"}]
		    						}
		    						},editor:{
										xtype:'combo',
										field:'SK_OUTTYPE',
										queryMode: 'local',
							    		displayField: 'display',
							    		valueField: 'value',
							    		editable:false,
							    		store:{fields: ['display', 'value'],
			    							   data:[{display: "转出货通知单", value: "TURNSN"},{display: "转出货单", value: "TURNOUT"}]
			    						}
									},renderer:function(val){
			    						if(val != null && val.toString().trim() != ''){
			    							switch (val) {
								    					case 'TURNSN'		:rVal = "转出货通知单"; break;
								                        case 'TURNOUT'	:rVal = "转出货单";  break;
						                    }
						    				return rVal;
			    						} else {
			    							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
			    							'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
			    						}
			    					}},
						{dataIndex:'SK_ISSAMPLE',width: 40,text:'样品',xtype:'checkcolumn'},
						{dataIndex:'SK_STATUS',width: 0,text:'状态',value:'已审核'},
						{dataIndex:'SK_STATUSCODE',width: 0,text:'状态码',/*value:'AUDITED'*/renderer : function(value){
							if(value==null||value==''){
								value='AUDITED';
								return value;
								}else{
								     	return value;
								}
							}}
		    	],
		    	gridCondition:'1=1',
				saveUrl: 'scm/sale/saveSaleKind.action?caller=SaleKind',
				deleteUrl: 'scm/sale/deleteSaleKind.action?caller=SaleKind',
				updateUrl: 'scm/sale/updateSaleKind.action?caller=SaleKind',
				getIdUrl: 'common/getId.action?seq=SALEKIND_SEQ'
				 },
			   {flag:'Payments-gathering',table:'Payments',keyField:'PA_ID',codeField:'PA_CODE',caller:'Payments!Sale!saas',statusfield:'PA_AUDITSTATUS',statuscodefield:'PA_AUDITSTATUSCODE',
					fields:'PA_CODE,PA_ID,PA_MONTHADD,PA_DAYADD,PA_NAME,PA_STATUSCODE,PA_AUDITSTATUS,PA_AUDITDATE,PA_CLASS,PA_VALID,PA_CREDITCONTROL',
					relfields:"PA_NAME,PA_CLASS",seq:'Payments_SEQ',columns:[{dataIndex:'PA_ID',width: 0,text:'ID'},{dataIndex:'PA_CODE',width:100,text:'收款方式编号'},
						     {dataIndex:'PA_MONTHADD',width: 60,text:'月增加',editor:{xtype:'textfield',field:'PA_MONTHADD'}},
						     {dataIndex:'PA_DAYADD',width: 60,text:'日增加',editor:{xtype:'textfield',field:'PA_DAYADD'}},
						     {dataIndex:'PA_CREDITCONTROL',width: 0,text:'额度管控'},
						     {dataIndex:'PA_VALID',width: 0,text:'是否有效'},
						     {dataIndex:'PA_NAME',width: 100,text:'收款方式名称',logic: 'necessaryField',editor:{xtype:'textfield',field:'PA_NAME'}},
						     {dataIndex:'PA_CLASS',width: 80,text:'&nbsp单据类型'/*,editor:{xtype:'textfield',field:'PA_CLASS'}*/},
						     {dataIndex:'PA_AUDITSTATUS',width: 0,text:'状态',editor:{xtype:'textfield',field:'PA_AUDITSTATUS'},value:'已审核'},
						     {dataIndex:'PA_AUDITSTATUSCODE',width: 0,text:'状态编号',editor:{xtype:'textfield',field:'PA_AUDITSTATUSCODE'},value:'AUDITED'}
			    	],
			    	gridCondition:'pa_class=\'收款方式\'',
			    	saveUrl : 'scm/sale/savePayments.action',
					deleteUrl : 'scm/sale/deletePayments.action',
					updateUrl : 'scm/sale/updatePayments.action',
					getIdUrl: 'common/getId.action?seq=PAYMENTS_SEQ'
					 },
			   {flag:'Bom-level',table:'Bomlevel',keyField:'BL_ID',codeField:'BL_CODE',caller:'Bomlevel!saas',statusfield:'BL_STATUS',statuscodefield:'BL_STATUSCODE',
					fields:'BL_STATUSCODE,BL_STATUS,BL_INDATE,BL_IFMRP,BL_MPBOM,BL_RECORDOR,BL_REMARK,BL_CODE,BL_ID',
					relfields:"BL_REMARK",seq:'Bomlevel_SEQ',columns:[{dataIndex:'BL_ID',width: 0,text:'ID'},{dataIndex:'BL_CODE',width: 0,text:'物料等级编号'},
						     {dataIndex:'BL_REMARK',width: 100,text:'&nbsp&nbsp等级名称',logic: 'necessaryField',editor:{xtype:'textfield',field:'BL_REMARK'}},
						     {dataIndex:'BL_IFMRP',width: 80,text:'参与MRP运算',xtype:'checkcolumn'},
						     {dataIndex:'BL_MPBOM',width: 40,text:'量产',xtype:'checkcolumn'},
						     {dataIndex:'BL_STATUS',width: 0,text:'状态',editor:{xtype:'textfield',field:'BL_STATUS'},value:'已审核'},
						     {dataIndex:'BL_STATUSCODE',width: 0,text:'状态码',editor:{xtype:'textfield',field:'BL_STATUSCODE'},value:'AUDITED'}
			    	],
			    	gridCondition:'1=1',
			    	saveUrl: 'pm/bom/saveBomlevel.action',
					deleteUrl: 'pm/bom/deleteBomlevel.action',
					updateUrl: 'pm/bom/updateBomlevel.action',
					getIdUrl: 'common/getCommonId.action?caller=Bomlevel',
					defaultItems:[]
					 },
			   {flag:'Purchase-Kind',table:'PurchaseKind',keyField:'PK_ID',codeField:'PK_CODE',caller:'PurchaseKind!saas',statusfield:'PK_STATUS',statuscodefield:'PK_STATUSCODE',
							fields:'PK_CODE,PK_ID,PK_NAME,PK_MRP,PK_ALLOWNULLPRICE,PK_IFLACK,PK_ALLOWNOAPPSTATUS,PK_ALLOWIN,PK_STATUS,PK_STATUSCODE,PK_IFMRPKIND,PK_IFCUSTOFFER',
							relfields:"PK_NAME",seq:'PurchaseKind_SEQ',columns:[{dataIndex:'PK_ID',width: 0,text:'ID'},{dataIndex:'PK_CODE',width: 0,text:'类型编号'},
								     {dataIndex:'PK_NAME',width: 100,text:'类型名称',logic: 'necessaryField',editor:{xtype:'textfield',field:'PK_NAME'}},
								     {dataIndex:'PK_MRP',width: 80,text:'参与MRP运算',xtype:'checkcolumn'},
								     {dataIndex:'PK_IFLACK',width: 85,text:'参与缺料运算',xtype:'checkcolumn'},
								     {dataIndex:'PK_ALLOWNOAPPSTATUS',width: 100,text:'允许未认可物料',xtype:'checkcolumn'},
								     {dataIndex:'PK_IFCUSTOFFER',width: 0,text:'是否客供',xtype:'checkcolumn'},
								     {dataIndex:'PK_ALLOWNULLPRICE',width: 70,text:'允许0单价',xtype:'checkcolumn'},
								     {dataIndex:'PK_ALLOWIN',width: 60,text:'直接入库',xtype:'checkcolumn'},
								     {dataIndex:'PK_IFMRPKIND',width: 80,text:'MRP默认类型',xtype:'checkcolumn'},
								     {dataIndex:'PK_STATUS',width: 0,text:'状态',editor:{xtype:'textfield',field:'PK_STATUS'},value:'已审核'},
								     {dataIndex:'PK_STATUSCODE',width: 0,text:'状态码',editor:{xtype:'textfield',field:'PK_STATUSCODE'},value:'AUDITED'}
					    	],
					    	gridCondition:'1=1',
							saveUrl: 'scm/sale/savePurchaseKind.action',
							deleteUrl: 'scm/sale/deletePurchaseKind.action',
							updateUrl: 'scm/sale/updatePurchaseKind.action',
							getIdUrl: 'common/getCommonId.action?caller=PurchaseKind',
							defaultItems:[]
							 },
			   {flag:'Payments-pay',table:'Payments',keyField:'PA_ID',codeField:'PA_CODE',caller:'Payments!Purchase!saas',statusfield:'PA_AUDITSTATUS',statuscodefield:'PA_AUDITSTATUSCODE',
							fields:'PA_CODE,PA_MONTHADD,PA_DAYADD,PA_NAME,PA_AUDITSTATUS,PA_CLASS,PA_ID,PA_CREDITCONTROL,PA_VALID',
							relfields:"PA_NAME,PA_CLASS",seq:'Payments_SEQ',columns:[{dataIndex:'PA_ID',width: 0,text:'ID'},{dataIndex:'PA_CODE',width:100,text:'付款方式编号'},
								     {dataIndex:'PA_MONTHADD',width: 60,text:'月增加',editor:{xtype:'textfield',field:'PA_MONTHADD'}},
								     {dataIndex:'PA_DAYADD',width: 60,text:'日增加',editor:{xtype:'textfield',field:'PA_DAYADD'}},
								     {dataIndex:'PA_NAME',width: 100,text:'付款方式名称',logic: 'necessaryField',editor:{xtype:'textfield',field:'PA_NAME'}},
								     {dataIndex:'PA_AUDITSTATUS',width: 0,text:'单据状态'/*,editor:{xtype:'textfield',field:'PA_AUDITSTATUS'}*/},
								     {dataIndex:'PA_CLASS',width: 80,text:'&nbsp单据类型'/*,editor:{xtype:'textfield',field:'PA_CLASS'}*/},
								     {dataIndex:'PA_CREDITCONTROL',width: 0,text:'额度管控'},
								     {dataIndex:'PA_VALID',width: 0,text:'是否有效'},
								     {dataIndex:'PA_AUDITSTATUS',width: 0,text:'状态',editor:{xtype:'textfield',field:'PA_AUDITSTATUS'},value:'已审核'},
								     {dataIndex:'PA_AUDITSTATUSCODE',width: 0,text:'状态码',editor:{xtype:'textfield',field:'PA_AUDITSTATUSCODE'},value:'AUDITED'}
					    	],
					    	gridCondition:'pa_class=\'付款方式\'',
					    	saveUrl: 'scm/purchase/savePayments.action',
							deleteUrl: 'scm/purchase/deletePayments.action',
							updateUrl: 'scm/purchase/updatePayments.action',
							getIdUrl: 'common/getId.action?seq=PAYMENTS_SEQ',
							defaultItems:[]
							 },
			   {flag:'FeeCategorySet-Base',table:'FeeCategorySet',keyField:'FCS_ID',codeField:'FCS_CODE',caller:'FeeCategorySet!saas',statusfield:'',statuscodefield:'',
				fields:'FCS_ID,FCS_ITEMNAME,FCS_ITEMDESCRIPTION,FCS_DEPARTMENTCODE,FCS_DEPARTMENTNAME,FCS_CATECODE,FCS_CLASS,FCS_STATUS,FCS_CATENAME,FCS_ORNAME,FCS_CODE',
				relfields:"FCS_ITEMNAME",seq:'FeeCategorySet_SEQ',columns:[{dataIndex:'FCS_ID',width: 0,text:'ID'},{dataIndex:'FCS_CODE',width: 0,text:'单据编号'},
					     {dataIndex:'FCS_CLASS',width: 140,text:'单据类型',xtype:'combocolumn',logic: 'necessaryField',
							 	filter:{xtype: 'combo',queryMode: 'local',filterName:'FCS_CLASS',
		    						displayField: 'display',valueField: 'value',
		    						store:{fields: ['display', 'value'],
		    							   data:[{display: "业务招待费报销单", value: "业务招待费报销单"},{display: "业务招待费申请单", value: "业务招待费申请单"}, {display: "银行付款申请单", value: "银行付款申请单"}, {display: "出租车费申请单", value: "出租车费申请单"}, {display: "低值易耗品购置申请单", value: "低值易耗品购置申请单"}, {display: "费用报销单", value: "费用报销单"}, {display: "公司印章使用申请单", value: "公司印章使用申请单"},{display: "固定资产申请单", value: "固定资产申请单"},{display: "借款申请单", value: "借款申请单"},{display: "差旅费报销单", value: "差旅费报销单"},{display: "出差申请单", value: "出差申请单"}]
		    						}
		    						},editor:{
										xtype:'combo',
										field:'FCS_CLASS',
										queryMode: 'local',
							    		displayField: 'display',
							    		valueField: 'value',
							    		editable:false,
							    		store:{fields: ['display', 'value'],
			    							   data:[{display: "业务招待费报销单", value: "业务招待费报销单"},{display: "业务招待费申请单", value: "业务招待费申请单"}, {display: "银行付款申请单", value: "银行付款申请单"}, {display: "出租车费申请单", value: "出租车费申请单"}, {display: "低值易耗品购置申请单", value: "低值易耗品购置申请单"}, {display: "费用报销单", value: "费用报销单"}, {display: "公司印章使用申请单", value: "公司印章使用申请单"},{display: "固定资产申请单", value: "固定资产申请单"},{display: "借款申请单", value: "借款申请单"},{display: "差旅费报销单", value: "差旅费报销单"},{display: "出差申请单", value: "出差申请单"}]
			    						}
									}},
						 {dataIndex:'FCS_ITEMNAME',width: 150,text:'费用类型',logic: 'necessaryField',editor:{xtype:'textfield',field:'FCS_ITEMNAME'}},
						 {dataIndex:'FCS_ORNAME',width: 100,text:'组织名称',logic: 'necessaryField',editor:{xtype:'textfield',field:'FCS_ORNAME'}},
						 {dataIndex:'FCS_DEPARTMENTCODE',width: 100,text:'部门编号',logic: 'necessaryField',editor:{xtype:'textfield',field:'FCS_DEPARTMENTCODE'}},
						 {dataIndex:'FCS_DEPARTMENTNAME',width: 100,text:'部门名称',editor:{xtype:'textfield',field:'FCS_DEPARTMENTNAME'}},
						 {dataIndex:'FCS_CATECODE',width: 100,text:'科目编号',logic: 'necessaryField',editor:{xtype:'textfield',field:'FCS_CATECODE'}},
						 {dataIndex:'FCS_CATENAME',width: 100,text:'科目名称',editor:{xtype:'textfield',field:'FCS_CATENAME'}},
						 {dataIndex:'FCS_ITEMDESCRIPTION',width: 200,text:'项目描述',editor:{xtype:'textfield',field:'FCS_ITEMDESCRIPTION'}},
						 {dataIndex:'FCS_STATUS',width: 0,text:'状态',editor:{xtype:'textfield',field:'FCS_STATUS'},value:'已审核'},
					     {dataIndex:'FCS_STATUSCODE',width: 0,text:'状态码',editor:{xtype:'textfield',field:'FCS_STATUSCODE'},value:'AUDITED'}
		    	],
		    	gridCondition:'1=1',
		    	saveUrl: 'fa/fp/saveFeeCategorySet.action',
				deleteUrl: 'fa/fp/deleteFeeCategorySet.action',
				updateUrl: 'fa/fp/updateFeeCategorySet.action',
				getIdUrl: 'common/getId.action?seq=FeeCategorySet_SEQ',
				defaultItems:[]
				 },
	 		   {flag:'Warehouse-Base',table:'Warehouse',keyField:'WH_ID',codeField:'WH_CODE',caller:'Warehouse!Base!saas',statusfield:'WH_STATUS',statuscodefield:'WH_STATUSCODE',
				fields:'WH_CODE,WH_ID,WH_TYPE,WH_DESCRIPTION,WH_STATUS,WH_STATUSCODE,WH_IFMRP,WH_NOCOST,WH_RECORDER,WH_DATE,WH_BONDED,WH_IFMOVE,WH_IFVENDBAD,WH_IFDEFECT,WH_IFLACK,WH_IFWIP,WH_ISNOTIN,WH_ISNOTOUT,WH_IFCLASH,WH_IFOUTMAKE,WH_IFBARCODE,WH_IFB2C',
				relfields:"WH_DESCRIPTION",seq:'Warehouse_SEQ',
				columns:[{dataIndex:'WH_ID',width: 0,text:'ID'},
				         {dataIndex:'WH_CODE',width: 0,text:'仓库编号'},
					     {dataIndex:'WH_DESCRIPTION',width: 120,text:'仓库名称',logic: 'necessaryField',
					     	editor:{xtype:'textfield',field:'WH_DESCRIPTION'}},
					     {dataIndex:'WH_TYPE',width:0,text:'仓库类型',xtype:'combocolumn',
							 	filter:{xtype: 'combo',queryMode: 'local',filterName:'WH_TYPE',
			    						displayField: 'display',valueField: 'value',
			    						store:{fields: ['display', 'value'],
			    							   data:[{display: "良品仓", value: "良品仓"},{display: "不良品仓", value: "不良品仓"}, {display: "暂收仓", value: "暂收仓"}]
			    						}
			    				},
			    				editor:{
									xtype:'combo',
									field:'WH_TYPE',
									queryMode: 'local',
						    		displayField: 'display',
						    		valueField: 'value',
						    		editable:false,
									store:{fields: ["display", "value"],
										   data:[{display: "良品仓", value: "良品仓"},{display: "不良品仓", value: "不良品仓"}, {display: "暂收仓", value: "暂收仓"}]
			    					}
								}},
			    		{dataIndex:'WH_IFLACK',width:80,text:'参与缺料运算',xtype: "checkcolumn"},
			    		{dataIndex:'WH_IFCLASH',width:40,text:'冲销',xtype: "checkcolumn"},
			    		{dataIndex:'WH_NOCOST',width:60,text:'无值仓',xtype: "checkcolumn"},
			    		{dataIndex:'WH_IFOUTMAKE',width:0,text:'委外仓',xtype: "checkcolumn"},
			    		{dataIndex:'WH_ISNOTIN',width:0,text:'禁止入仓',xtype: "checkcolumn"},
			    		{dataIndex:'WH_ISNOTOUT',width:0,text:'禁止出仓',xtype: "checkcolumn"},
			    		{dataIndex:'WH_BONDED',width:40,text:'保税',xtype: "checkcolumn"},
			    		{dataIndex:'WH_IFMOVE',width:60,text:'挪料仓',xtype: "checkcolumn"},
			    		{dataIndex:'WH_IFVENDBAD',width:80,text:'来料不良仓',xtype: "checkcolumn"},
			    		{dataIndex:'WH_IFDEFECT',width:60,text:'不良品仓',xtype: "checkcolumn"},
			    		{dataIndex:'WH_IFWIP',width:60,text:'WIP仓',xtype: "checkcolumn"},
			    		{dataIndex:'WH_STATUS',width: 0,text:'状态',editor:{xtype:'textfield',field:'WH_STATUS'},value:'已审核'},
					    {dataIndex:'WH_STATUSCODE',width: 0,text:'状态码',editor:{xtype:'textfield',field:'WH_STATUSCODE'},value:'AUDITED'}
		    	],
		    	gridCondition:"WH_DESCRIPTION<>'IQC判退仓'",
		    	saveUrl: 'scm/saveWarehouse.action?caller=MakeKind!saas',
				deleteUrl: 'scm/deleteWarehouse.action?caller=MakeKind!saas',
				updateUrl: 'scm/updateWarehouse.action?caller=MakeKind!saas',
				getIdUrl: 'common/getId.action?seq=WAREHOUSE_SEQ'
	 		    },
			   {flag:'Make-Kind',table:'MakeKind',keyField:'MK_ID',codeField:'MK_CODE',caller:'MakeKind!saas',statusfield:'',statuscodefield:'',
				fields:'MK_CODE,MK_NAME,MK_MRP,MK_CLASHSALE,MK_ISUSE,MK_BATCHTYPE,MK_IFMRPNEED,MK_IFMRPNEEDMRP,MK_TYPE,MK_PRICE,MK_MAKIND,MK_ID,MK_IFMRPKIND,MK_FINISHUNGET',
				relfields:"MK_NAME",seq:'MakeKind_SEQ',columns:[{dataIndex:'MK_ID',width: 0,text:'ID'},
				        {dataIndex:'MK_CODE',width: 0,text:'类型编号',editor:{xtype:'textfield',field:'MK_CODE'}},
				        {dataIndex:'MK_NAME',width: 100,text:'类型名称',logic: 'necessaryField',editor:{xtype:'textfield',field:'MK_NAME'}},
					    {dataIndex:'MK_IFMRPNEED',width:80,text:'MRP料算需求',xtype:'checkcolumn'},
					    {dataIndex:'MK_MRP',width:80,text:'参与MRP供应',xtype:'checkcolumn'},
					    {dataIndex:'MK_IFMRPNEEDMRP',width:80,text:'所有料算需求',xtype:'checkcolumn'},
					    {dataIndex:'MK_CLASHSALE',width:60,text:'参与冲销',xtype:'checkcolumn'},
					    {dataIndex:'MK_PRICE',width:80,text:'单价允许为0',xtype:'checkcolumn'},
					    {dataIndex:'MK_FINISHUNGET',width:0,text:'未领料允许完工',xtype:'checkcolumn'},
					    {dataIndex:'MK_MAKIND',width: 60,text:'制造类型',xtype:'combocolumn',logic: 'necessaryField',
					    	filter:{xtype: 'combo',queryMode: 'local',filterName:'MK_MAKIND',
		    					displayField: 'display',valueField: 'value',
		    					store:{fields: ['display', 'value'],
		    						data:[{display: "制造", value: "MAKE"},{display: "委外", value: "OSMAKE"}]
		    					}
		    					},editor:{
									xtype:'combo',
									field:'MK_MAKIND',
									queryMode: 'local',
						    		displayField: 'display',
						    		valueField: 'value',
						    		editable:false,
						    		store:{fields: ['display', 'value'],
			    						data:[{display: "制造", value: "MAKE"},{display: "委外", value: "OSMAKE"}]
			    					}
		    					},
		    					renderer:function(val){
		    						if(val != null && val.toString().trim() != ''){
		    							switch (val) {
							    					case 'MAKE'		:rVal = "制造"; break;
							                        case 'OSMAKE'	:rVal = "委外";  break;
					                    }
					    				return rVal;
		    						} else {
		    							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
		    							'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
		    						}
					    				
		    					}},
    					{dataIndex:'MK_TYPE',width: 60,text:'加工类型',xtype:'combocolumn',logic: 'necessaryField',
					    	filter:{xtype: 'combo',queryMode: 'local',filterName:'MK_TYPE',
		    					displayField: 'display',valueField: 'value',
		    					store:{fields: ['display', 'value'],
		    						data:[{display: "标准", value: "S"},{display: "返修", value: "R"},{display: "拆件", value: "D"}]
		    					}
		    					},editor:{
									xtype:'combo',
									field:'MK_TYPE',
									queryMode: 'local',
						    		displayField: 'display',
						    		valueField: 'value',
						    		editable:false,
						    		store:{fields: ['display', 'value'],
			    						data:[{display: "标准", value: "S"},{display: "返修", value: "R"},{display: "拆件", value: "D"}]
			    					}
		    					},renderer:function(val){
		    						if(val != null && val.toString().trim() != ''){
		    							switch (val) {
							    					case 'S'		:rVal = "标准"; break;
							                        case 'R'	:rVal = "返修";  break;
							                        case 'D'		:rVal = "拆件"; break;
					                    }
					    				return rVal;
		    						} else {
		    							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
		    							'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
		    						}
					    				
		    					}},		
    					{dataIndex:'MK_BATCHTYPE',width: 60,text:'批量类型',xtype:'combocolumn',logic: 'necessaryField',
					    	filter:{xtype: 'combo',queryMode: 'local',filterName:'MK_BATCHTYPE',
		    					displayField: 'display',valueField: 'value',
		    					store:{fields: ['display', 'value'],
		    						data:[{display: "量产", value: "BATCH"},{display: "试产", value: "TEST"}]
		    					}
		    					},editor:{
									xtype:'combo',
									field:'MK_BATCHTYPE',
									queryMode: 'local',
						    		displayField: 'display',
						    		valueField: 'value',
						    		editable:false,
						    		store:{fields: ['display', 'value'],
			    						data:[{display: "量产", value: "BATCH"},{display: "试产", value: "TEST"}]
			    					}
		    					},renderer:function(val){
		    						if(val != null && val.toString().trim() != ''){
		    							switch (val) {
							    					case 'BATCH'		:rVal = "量产"; break;
							                        case 'TEST'	:rVal = "试产";  break;
					                    }
					    				return rVal;
		    						} else {
		    							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
		    							'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
		    						}
					    				
		    					}},
					    {dataIndex:'MK_IFMRPKIND',width:80,text:'MRP默认投放',xtype:'checkcolumn'},
					    {dataIndex:'MK_ISUSE',width:1,text:'是否有效'}
					    /*{dataIndex:'MK_STATUS',width: 0,text:'状态'},
					    {dataIndex:'MK_STATUSCODE',width: 0,text:'状态码'}*/
					    ],
				    gridCondition:'1=1',
				    saveUrl: 'pm/make/saveMakeKind.action?caller=MakeKind!saas',
					deleteUrl: 'pm/make/deleteMakeKind.action?caller=MakeKind!saas',
					updateUrl: 'pm/make/updateMakeKind.action?caller=MakeKind!saas',
					getIdUrl: 'common/getId.action?seq=MAKEKIND_SEQ',
					defaultItems:[]
						 },
			   {flag:'Currencys-Base',table:'Currencys',keyField:'CR_ID',codeField:'CR_CODE',caller:'Currencys!saas',statusfield:'',statuscodefield:'',
					fields:'CR_ID,CR_CODE,CR_NAME,CR_RATE,CR_VORATE,CR_STATUS,CR_STATUSCODE,CR_TAXRATE',
					relfields:"CR_NAME",seq:'Currencys_SEQ',columns:[{dataIndex:'CR_CODE',width: 0,text:'编号'},
						     {dataIndex:'CR_NAME',width: 120,text:'币别',logic: 'necessaryField',editor:{xtype:'textfield',field:'CR_NAME'}},
							 {dataIndex:'CR_RATE',width: 120,text:'汇率',editor:{xtype:'textfield',field:'CR_RATE'}},
			    			 {dataIndex:'CR_VORATE',width: 120,text:'记账汇率',editor:{xtype:'textfield',field:'CR_VORATE'}},
			    			 {dataIndex:'CR_TAXRATE',width: 150,text:'默认税率(%)',editor:{xtype:'textfield',field:'CR_TAXRATE'}},
			    			 {dataIndex:'CR_ID',width: 80,text:'ID',editor:{xtype:'numberfield',field:'CR_ID'}},
			    			 {dataIndex:'CR_STATUS',width: 0,text:'状态',editor:{xtype:'textfield',field:'CR_STATUS'},value:'已审核'},
							 {dataIndex:'CR_STATUSCODE',width: 0,text:'状态码',editor:{xtype:'textfield',field:'CR_STATUSCODE'},value:'AUDITED'}
			    			 ],
			    	gridCondition:'1=1',
			    	saveUrl: 'fa/ars/saveCurrencys.action?caller=Currencys!saas',
					deleteUrl: 'fa/ars/deleteCurrencys.action?caller=Currencys!saas',
					updateUrl: 'fa/ars/updateCurrencys.action?caller=Currencys!saas',
					getIdUrl: 'common/getId.action?seq=CURRENCYS_SEQ',
					defaultItems:[]
			    },
			   {flag:'Category-Base',table:'Category',keyField:'CA_ID',codeField:'CA_CODE',caller:'Category!Base!saas',statusfield:'CA_STATUS',statuscodefield:'CA_STATUSCODE',
					fields:'CA_NAME,CA_DESCRIPTION,CA_LEVEL,CA_TYPE,CA_BALANCETYPE,CA_CLASS,CA_CURRENCYTYPE,CA_CURRENCY,CA_ASSNAME,CA_CHECKRATE,CA_CASHFLOW,CA_ISLEAF,CA_ISCASH,CA_ISBANK,CA_ISCASHBANK,CA_PCODE,CA_ID,CA_CODE,CA_STATUS,CA_STATUSCODE',
					relfields:"CA_NAME",seq:'Category_SEQ',columns:[{dataIndex:'CA_CODE',width: 0,text:'编号'},
					         {dataIndex:'CA_ID',width: 0,text:'ID',editor:{xtype:'numberfield',field:'CA_ID'}},
						     {dataIndex:'CA_NAME',width: 100,text:'科目名称',logic: 'necessaryField',editor:{xtype:'textfield',field:'CA_NAME'}},
							 {dataIndex:'CA_DESCRIPTION',width: 120,text:'科目描述',editor:{xtype:'textfield',field:'CA_DESCRIPTION'}},
			    			 {dataIndex:'CA_LEVEL',width: 80,text:'层次',editor:{xtype:'textfield',field:'CA_LEVEL'}},
			    			 {dataIndex:'CA_PCODE',width: 80,text:'父级科目',editor:{xtype:'textfield',field:'CA_PCODE'}},
			    			 {dataIndex:'CA_TYPE',width: 100,text:'科目性质',xtype:'combocolumn',
							    	filter:{xtype: 'combo',queryMode: 'local',filterName:'CA_TYPE',
				    					displayField: 'display',valueField: 'value',
				    					store:{fields: ['display', 'value'],
				    						data:[{display: "借方", value: "0"},{display: "贷方", value: "1"},{display: "借方或贷方", value: "2"}]
				    					}
				    					},editor:{
											xtype:'combo',
											field:'CA_TYPE',
											queryMode: 'local',
								    		displayField: 'display',
								    		valueField: 'value',
								    		editable:false,
								    		store:{fields: ['display', 'value'],
					    						data:[{display: "借方", value: "0"},{display: "贷方", value: "1"},{display: "借方或贷方", value: "2"}]
					    					}
				    					}},
	    					{dataIndex:'CA_BALANCETYPE',width: 100,text:'余额方向',xtype:'combocolumn',
						    	filter:{xtype: 'combo',queryMode: 'local',filterName:'CA_BALANCETYPE',
			    					displayField: 'display',valueField: 'value',
			    					store:{fields: ['display', 'value'],
			    						data:[{display: "借方", value: "0"},{display: "贷方", value: "1"},{display: "借方或贷方", value: "2"}]
			    					}
			    					},editor:{
										xtype:'combo',
										field:'CA_TYPE',
										queryMode: 'local',
							    		displayField: 'display',
							    		valueField: 'value',
							    		editable:false,
							    		store:{fields: ['display', 'value'],
				    						data:[{display: "借方", value: "0"},{display: "贷方", value: "1"},{display: "借方或贷方", value: "2"}]
				    					}
			    					}},
	    					 {dataIndex:'CA_CLASS',width: 100,text:'科目类型',xtype:'combocolumn',logic: 'necessaryField',
						    	filter:{xtype: 'combo',queryMode: 'local',filterName:'CA_CLASS',
			    					displayField: 'display',valueField: 'value',
			    					store:{fields: ['display', 'value'],
			    						data:[{display: "资产", value: "资产"},{display: "负债", value: "负债"},{display: "所有者权益", value: "所有者权益"},{display: "成本", value: "成本"},{display: "损益", value: "损益"}]
			    					}
			    					},editor:{
										xtype:'combo',
										field:'CA_CLASS',
										queryMode: 'local',
							    		displayField: 'display',
							    		valueField: 'value',
							    		editable:false,
							    		store:{fields: ['display', 'value'],
				    						data:[{display: "资产", value: "资产"},{display: "负债", value: "负债"},{display: "所有者权益", value: "所有者权益"},{display: "成本", value: "成本"},{display: "损益", value: "损益"}]
				    					}
			    					}},
			    			 {dataIndex:'CA_CURRENCY',width: 80,text:'币别',editor:{xtype:'textfield',field:'CA_CURRENCY'}},
			    			 {dataIndex:'CA_CURRENCYTYPE',width: 80,text:'外币核算',editor:{xtype:'textfield',field:'CA_CURRENCYTYPE'}},
			    			 {dataIndex:'CA_ASSTYPE',width: 80,text:'辅助核算',editor:{xtype:'textfield',field:'CA_ASSTYPE'}},
			    			 {dataIndex:'CA_CHECKRATE',width: 80,text:'期末调汇',xtype:'checkcolumn'},
			    			 {dataIndex:'CA_CASHFLOW',width: 80,text:'现金流量相关',xtype:'checkcolumn'},
			    			 {dataIndex:'CA_ISLEAF',width: 80,text:'末级',xtype:'checkcolumn'},
			    			 {dataIndex:'CA_ISCASH',width: 80,text:'现金科目',xtype:'checkcolumn'},
			    			 {dataIndex:'CA_ISBANK',width: 80,text:'银行科目',xtype:'checkcolumn'},
			    			 {dataIndex:'CA_ISCASHBANK',width: 80,text:'为现金银行',xtype:'checkcolumn'},
			    			 {dataIndex:'CA_STATUS',width: 0,text:'状态',editor:{xtype:'textfield',field:'CA_STATUS'},value:'已审核'},
							 {dataIndex:'CA_STATUSCODE',width: 0,text:'状态码',editor:{xtype:'textfield',field:'CA_STATUSCODE'},value:'AUDITED'}
			    			 ],
			    	gridCondition:'1=1',
			    	saveUrl: 'fa/ars/saveCategoryBase.action?caller=Category!Base!saas',
					deleteUrl: 'fa/ars/deleteCategoryBase.action?caller=Category!Base!saas',
					updateUrl: 'fa/ars/updateCategoryBase.action?caller=Category!Base!saas',
					getIdUrl: 'common/getId.action?seq=CATEGORY_SEQ',
					defaultItems:[]
			 }],
	getItmes:function(){
		var me=this;
		var auto=false;
		Ext.each(me.configure,function(conf){
			if(conf.flag==me.flag){
				Ext.each(conf.columns,function(c){
				if(c.logic&&c.logic=='necessaryField'&&!c.renderer){
					c.renderer=function(val){
						if(val != null && val.toString().trim() != ''){
							return val;
						} else {
							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
							'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
						}
					};
				}
				});
				gridcolumns=Ext.Array.merge(
				[{dataIndex:'ENABLE',width: 43,text:'启用',fixed:true,xtype: "",
				filter: {dataIndex: "ENABLE", xtype: "textfield"},
				  renderer: function(value, cellmeta, record) {
					  var keyfield=this.keyField;
					  var keyvalue=record.data[this.keyField];
					  var flag=this.ownerCt.flag;
				  	  cellmeta.style="padding:0px!important";
					  if(value==1){
					  	return '<input class="mui-switch" onchange="changeEnable(this)"  type="checkbox" ' +
					  			'name="'+flag+'" data-id="'+keyvalue+'" checked>';
					  }else{
					  	return '<input class="mui-switch" onchange="changeEnable(this)"  type="checkbox" ' +
					  			'name="'+flag+'" data-id="'+keyvalue+'">';
					  }
				  }
				},{text:'保存',xtype: 'actioncolumn',width: 35, tooltip: '保存',icon:window.basePath+'jsps/sys/images/save_default.png',
					fixed:true,handler:function(grid, rowIndex, colIndex,item){	
						var record = grid.getStore().getAt(rowIndex),gridpanel=grid.ownerCt;
						if(record.data.ENABLE!=0){
							if(record.dirty){
							var modified = Ext.Object.getKeys(record.modified).join(",");
							var rr=new Object();
							var cansave=true;
							Ext.each(gridpanel.columns,function(c){
								if(c.dataIndex&&c.dataIndex!='ENABLE'){
									if(c.xtype == 'numberfield'){//number类型赋默认值，不然sql无法执行
										if(record.data[c.dataIndex] == null || record.data[c.dataIndex] == ''){
											record.data[c.dataIndex]=0;
										}
									}else if(c.xtype =='checkcolumn'){
										if(record.data[c.dataIndex]){
											record.data[c.dataIndex]=-1;
										}else{
											record.data[c.dataIndex]=0;
										}
									}
									if(c.text=='额度管控'){
										record.data[c.dataIndex]='否';
									}
									if(c.text=='是否有效'){
										record.data[c.dataIndex]=-1;
									}
									if(c.dataIndex=='SK_ALLOWZERO'){
										record.data[c.dataIndex]=1;
									}
									if(c.dataIndex==gridpanel.statusfield){
										record.data[c.dataIndex]='已审核';
									}
									if(c.dataIndex==gridpanel.statuscodefield){
										record.data[c.dataIndex]='AUDITED';
									}
									if(c.logic&&c.logic=='necessaryField'&&
										(record.data[c.dataIndex]==null||record.data[c.dataIndex]=="")){
											showResult('提示',c.text+'不能为空');
											cansave=false;
											return false;
										}
										rr[c.dataIndex.toLocaleLowerCase()]=record.data[c.dataIndex];
									}
								});
							if(cansave){
								gridpanel.save(rr);
							}
							}else{
								showResult('提示','还未修改数据');return;
							}
						}
						
					}
				}],conf.columns);
				Ext.apply(me, { 
					items: [{ 
						region:'center',
						autoRender:true,
						xtype:'simpleactiongrid',
						table:conf.table,
						keyField:conf.keyField,
						columns:gridcolumns,
						fields:"ENABLE,"+conf.fields,
						relfields:conf.relfields,
						auto:auto,
						gridCondition:conf.gridCondition,
						saveUrl: conf.saveUrl,//保存
						deleteUrl: conf.deleteUrl,//删除
						updateUrl: conf.updateUrl,//修改
						getIdUrl: conf.getIdUrl,//获得主键值
						codeField:conf.codeField,//编号字段
						statusfield:conf.statusfield,
						statuscodefield:conf.statuscodefield,
						seq:conf.seq,
						caller:conf.caller
					}]
				}); 
			}
		});
	}
});