Ext.QuickTips.init();
Ext.define('erp.controller.co.cost.BatchDeal', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'common.batchDeal.Viewport','common.batchDeal.Form','common.batchDeal.GridPanel','core.trigger.AddDbfindTrigger',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField', 'core.form.SeparNumber'
     	],
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({ 
    		'erpBatchDealFormPanel': {
    			alladded: function(form){
    				var grid = Ext.getCmp('batchDealGridPanel');
    				me.resize(form, grid);
    				if(!form.tempStore){
						grid.columns[1].hide();
					}
    			}
    		},
    		'erpBatchDealGridPanel': {
    			reconfigure: function(grid){
    				var form = Ext.getCmp('dealform');
    				me.resize(form, grid);
    			}
    		},
    		'erpVastDealButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpVastAnalyseButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpVastPrintButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		/**
    		 * 取价
    		 */
    		'erpGetPriceButton':{
    			click: function(btn){
    				me.vastDeal("cost/getPrice.action", true, ['pi_type', 'pi_remark']);
    			}
    		},
    		/**
    		 * 费用分摊
    		 */
    		'erpFeeShareButton':{
    			click: function(btn){
    				me.vastDeal("cost/shareFee.action");
    			}
    		},
    		/**
    		 * 核算
    		 */
    		'erpBussAccountButton':{
    			click: function(btn){
    				me.vastDeal("cost/accountProdio.action", true, ['pi_type', 'pi_class']);
    			}
    		},
    		/**
    		 * 重置单价
    		 */
    		'erpRePriceButton':{
    			click: function(btn){
    				me.vastDeal("cost/resPrice.action");
    			}
    		},
    		/**
    		 * 保存
    		 */
    		'erpVastSaveButton':{
    			click: function(btn){
    				me.vastDeal("cost/batchUpdate.action");
    			}
    		},
    		'monthdatefield': {
    			afterrender: function(f) {
    				me.getCurrentYearmonth(f);
    			},
    			change: function(f) {
    				var c = Ext.getCmp('pi_date');
    				if(c) {
    					c.setMonthValue(f.value);
    				}
    			}
    		},
    		'combo[name=pi_fromcode]': {
    			afterrender: function(m) {
    				m.hide();
    				m.value = '';
    			}
    		},
    		'combo[name=pi_class]': {
    			change: function(m){
    				var f = Ext.getCmp('pi_fromcode');
					if(!Ext.isEmpty(m.value)) {
						if(m.value == '其它入库单'){
							f.show();
							f.setValue('$ALL');
						} else {
							f.hide();
							f.setValue(null);
						}
					}
				}
    		},
    		'erpConsistencyButton':{
    			click:function(btn){
    				var f = Ext.getCmp('pi_class'), piclass = f.value, url = "";
    				if(piclass == '拨入单' || piclass == '拨出单'){
    					url = "cost/consistencyCheck.action";
    				} else if(piclass == '销售拨入单' || piclass == '销售拨出单'){
    					url = "cost/consistencySaleCheck.action";
    				} else if(piclass == '$ALL') {
    					var b = f.store.findRecord('value', '拨入单');
    					if(b) {
    						url = "cost/consistencyCheck.action";
    					} else {
    						url = "cost/consistencySaleCheck.action";
    					}
    				}
    				Ext.Ajax.request({
    	    			url : basePath + url,
    	    			params:{
    	    				date: Ext.getCmp('pi_type').value
    	    			},
    	    			method:'post',
    	    			callback:function(options,success,response){
    	    				var localJson = new Ext.decode(response.responseText);
    	        			if(localJson.success){
    	        				Ext.Msg.alert("提示","操作成功！");
    	        			} else {
    	        				if(localJson.exceptionInfo){
    	        	   				var str = localJson.exceptionInfo;
    	        	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	        	   					str = str.replace('AFTERSUCCESS', '');
    	        	   					showError(str);
    	        	   					Ext.Msg.alert("提示","操作成功！");
    	        	   				} else {
    	        	   					showError(str);return;
    	        	   				}
    	        	   			}
    	        			}
    	    			}
    	    		});
    			}
    		},
    		'textfield[name=pi_class]': {
    			afterrender: function(m) {
    				var f = Ext.getCmp('pi_class');
					if(!Ext.isEmpty(m.value)) {
						switch (m.value) {
							case '其它入库单':
								me.getComboData(f.store, 'ProdInOut!OtherIn');
								break;
							case '其它出库单':
								me.getComboData(f.store, 'ProdInOut!OtherOut');
								break;
						}
					}
    			},
    			change: function(m){
    				if(typeof (f = Ext.getCmp('pi_fromcode')) != 'undefined'){
						if(!Ext.isEmpty(m.value)) {
							switch (m.value) {
								case '其它入库单':
									me.getComboData(f.store, 'ProdInOut!OtherIn');
									break;
								case '其它出库单':
									me.getComboData(f.store, 'ProdInOut!OtherOut');
									break;
							}
						}
    				}
				}
    		}
    	});
    },
    resize: function(form, grid){
    	if(!this.resized && form && grid && form.items.items.length > 0){
    		var height = window.innerHeight, 
    			fh = form.getEl().down('.x-panel-body>.x-column-inner').getHeight();
			form.setHeight(35 + fh);
			grid.setHeight(height - fh - 35);
			this.resized = true;
		}
    },
    countGrid: function(){
    	//重新计算合计栏值
    	var grid = Ext.getCmp('batchDealGridPanel');
    	Ext.each(grid.columns, function(column){
			if(column.summary){
				var sum = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						sum += Number(item.value);
					}
				});
				Ext.getCmp(column.dataIndex + '_sum').setText(column.text + '(sum):' + sum);
			} else if(column.average) {
				var average = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						average += Number(item.value);
					}
				});
				average = average/grid.store.data.items.length;
				Ext.getCmp(column.dataIndex + '_average').setText(column.text + '(average):' + average);
			} else if(column.count) {
				var count = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						count++;
					}
				});
				Ext.getCmp(column.dataIndex + '_count').setText(column.text + '(count):' + count);
			}
		});
    },
    /**
     * @param useCondition	允许按条件执行 
     * @param condParams	按条件执行时，作为额外条件传回的字段
     */
    vastDeal: function(url, useCondition, condParams){
    	var grid = Ext.getCmp('batchDealGridPanel');
    	var items = grid.getMultiSelected();
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		item.index = this.data[grid.keyField];
        		grid.multiselected.push(item);
        	}
        });
    	var form = Ext.getCmp('dealform');
		var records = Ext.Array.unique(grid.multiselected);
		var bool = false, params = new Object(), data = new Array();
		params.caller = caller;
		if(records.length > 0){
			Ext.each(records, function(record, index){
				var f = form.fo_detailMainKeyField;
				if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) 
	        		||(f && this.data[f] != null && this.data[f] != ''
		        		&& this.data[f] != '0' && this.data[f] != 0)){
					bool = true;
					var o = new Object();
					if(grid.keyField){
						o[grid.keyField] = record.data[grid.keyField];
					} else {
						params.id[index] = record.data[form.fo_detailMainKeyField];
					}
					if(grid.toField){
						Ext.each(grid.toField, function(f, index){
							var v = Ext.getCmp(f).value;
							if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
								o[f] = v;
							}
						});
					}
					if(grid.necessaryFields){
						Ext.each(grid.necessaryFields, function(f, index){
							var v = record.data[f];
							if(Ext.isDate(v)){
								v = Ext.Date.toString(v);
							}
							o[f] = v;
						});
					}
					data.push(o);
				}
			});
		} else if(useCondition) {
			params.condition = form.getCondition();
			bool = true;
			if(condParams) {
				var s = {};
				Ext.Array.each(condParams, function(p){
					s[p] = Ext.getCmp(p).getValue();
				});
				params.condParams = unescape(Ext.JSON.encode(s).replace(/\\/g,"%"));
			}
		}
		if(bool){
			params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
			var main = parent.Ext.getCmp("content-panel");
			main.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + url,
		   		params: params,
		   		method : 'post',
		   		timeout: 1200000,
		   		callback : function(opt, s, r){
		   			main.getActiveTab().setLoading(false);
		   			var rs = new Ext.decode(r.responseText);
		   			if(rs.exceptionInfo){
		   				var str = rs.exceptionInfo;
		   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
		   					str = str.replace('AFTERSUCCESS', '');
		   					grid.multiselected = new Array();
		   					Ext.getCmp('dealform').onQuery();
		   				}
		   				showError(str);return;
		   			} else if(rs.success){
	    				if(rs.log){
	    					showMessage("提示", localJson.log);
	    				}
		   				Ext.Msg.alert("提示", "处理成功!", function(){
		   					grid.multiselected = new Array();
		   					Ext.getCmp('dealform').onQuery();
		   				});
		   			}
		   		}
			});
		} else {
			showError("没有需要处理的数据!");
		}
    },
	getCurrentYearmonth: function(f) {
		Ext.Ajax.request({
			url : basePath + "fa/getMonth.action",
			params:{type:'MONTH-P'},
			method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.success){
        			Ext.getCmp('pi_type').setValue(res.data.PD_DETNO);
        		}
        	}
		});
	},
	getComboData: function(store, cal) {
		if(this._combodata && this._combodata[cal]) {
			store.loadData(this._combodata[cal]);
			return;
		}
		var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		async: false,
	   		params: {
	   			caller: 'DataListCombo',
	   			fields: 'dlc_value,dlc_display',
	   			condition: 'dlc_caller=\'' + cal + '\' AND dlc_fieldname=\'pi_type\''
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);return;
	   			}
    			if(localJson.success){
    				var data = Ext.decode(localJson.data), arr = new Array();
    				if(cal == 'ProdInOut!OtherIn' || cal == 'ProdInOut!OtherOut') {
    					arr.push({
    						display: '全部',
    						value: '$ALL'
    					});
    				}
    				for(var i in data) {
    					arr.push({
    						display: data[i].DLC_VALUE,
    						value: data[i].DLC_DISPLAY
    					});
    				}
    				store.loadData(arr);
    				if(me._combodata == null) {
    					me._combodata = {};
    				}
    				me._combodata[cal] = arr;
	   			}
	   		}
		});
	}
});