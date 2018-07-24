Ext.QuickTips.init();
Ext.define('erp.controller.oa.meeting.Showapply', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'common.batchDeal.Viewport','common.batchDeal.Form','oa.meeting.Showapply',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.button.Import','core.button.ImportAll'
     	],
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({ 
    		'erpBatchDealFormPanel': {
    			alladded: function(form){
    				var grid = Ext.getCmp('batchDealGridPanel');
    				me.resize(form, grid);
    			}
    		},
    		'erpBatchDealGridPanel': {
    			afterrender: function(grid){
    				var form = Ext.getCmp('dealform');
    				me.resize(form, grid);
    			}
    		},
    		'triggerfield[id=ma_date]':{
    			afterrender:function(){
    				var date=Ext.getCmp('ma_date').getValue();
    				var weekday = ["星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];
    				Ext.getCmp('ma_weekday').setValue(weekday[date.getDay()]);
    			},
    			change:function(){
    				var date=Ext.getCmp('ma_date').getValue();
    				var weekday = ["星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];
    				Ext.getCmp('ma_weekday').setValue(weekday[date.getDay()]);
    			}
    		},
    		/*'erpVastDealButton':{},*/
    		'button[id=query]':{    
    			beforerender:function(btn){
    				btn.handler=function(select){
    					var grid = Ext.getCmp('batchDealGridPanel'),sel = [];
    					if(!grid){
    						grid = Ext.getCmp('grid');
    					}
    					grid.multiselected = new Array();
    					if(select == true) {
    						sel = grid.selModel.getSelection();
    					}
    					var form = Ext.getCmp('dealform');
    					var cond = me.getCondition(grid);
    					if(Ext.isEmpty(cond)) {
    						cond = '1=1';
    					}
    					form.beforeQuery(caller, cond);//执行查询前逻辑
    					var gridParam = {caller: caller, condition: cond + form.getOrderBy(grid)};
    					if(grid.getGridColumnsAndStore){
    						grid.loadNewStore(grid, 'oa/meeting/singleGridPanel.action', gridParam, "");
    					}
    					if(select == true) {
    						Ext.each(sel, function(){
    							grid.selModel.select(this.index);
    						});
    					}
    				};
    			}
    		    
    		 },
    		'erpImportButton':{
    			click:function(btn){
    			  me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpImportAllButton':{
    			click:function(btn){
    				 me.vastDeal(btn.ownerCt.ownerCt.dealUrl,'all');	
    			}
    		}
    	});
    },
    resize: function(form, grid){
    	if(!this.resized && form && grid && form.items.items.length > 0){
			var height = window.innerHeight;
			var cw = 0;
			Ext.each(form.items.items, function(){
				if(!this.hidden && this.xtype != 'hidden') {
					cw += this.columnWidth;
				}
			});
			cw = Math.ceil(cw);
			if(cw == 0){
				cw = 5;
			} else if(cw > 2 && cw <= 5){
				cw -= 1;
			} else if(cw > 5 && cw < 8){
				cw = 4;
			}
			cw = Math.min(cw, 5);
			form.setHeight(height*cw/10 + 10);
			grid.setHeight(height*(10 - cw)/10 - 10);
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
    getCondition:function(grid){
    	grid = grid || Ext.getCmp('batchDealGridPanel');
		if(!grid){
			grid = Ext.getCmp('grid');
		}
		var form = Ext.getCmp('dealform');
		var condition = Ext.isEmpty(grid.defaultCondition) ? '' : ('(' + grid.defaultCondition + ')');
		Ext.each(form.items.items, function(f){
			if(f.name=='ml_kind'){
				if(f.value!=null&&f.value!=''){
				if(condition == ''){
					condition += f.value;
				} else {
					condition += ' AND ' + f.value;
				}
			   }
			}else {
			if(f.logic != null && f.logic != ''){
				if(f.xtype == 'checkbox' && f.value == true){
					if(condition == ''){
						condition += f.logic;
					} else {
						condition += ' AND ' + f.logic;
					}
				} else if(f.xtype == 'datefield' && f.value != null){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					}
				} else if(f.xtype == 'datetimefield' && f.value != null){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d H:i:s');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					}
				} else if(f.xtype == 'numberfield' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + '=' + f.value;
					} else {
						condition += ' AND ' + f.logic + '=' + f.value;
					}
				} else if(f.xtype == 'combo' && f.value == '$ALL'){
					if(f.store.data.length > 1) {
						if(condition == ''){
							condition += '(';
						} else {
							condition += ' AND (';
						}
						f.store.each(function(d, idx){
							if(d.data.value != '$ALL') {
								if(idx == 0){
									condition += f.logic + "='" + d.data.value + "'";
								} else {
									condition += ' OR ' + f.logic + "='" + d.data.value + "'";
								}
							}
						});
						condition += ')';
					}
				} else {
					//一般情况下，在执行批量处理时,是不需要把form的数据传回去,
					//但某些情况下，需要将form的某些字段的值也传回去
					//例如 请购批量转采购，如果指定了采购单号，就要把采购单号传回去
					if(contains(f.logic, 'to:', true)){
						if(!grid.toField){
							grid.toField = new Array();
						}
						grid.toField.push(f.logic.split(':')[1]);
					} else {
						if(!Ext.isEmpty(f.value)){
							if(contains(f.value.toString(), 'BETWEEN', true) && contains(f.value.toString(), 'AND', true)){
								if(condition == ''){
									condition += f.logic + " " + f.value;
								} else {
									condition += ' AND (' + f.logic + " " + f.value + ")";
								}
							} else if(contains(f.value.toString(), '||', true)){
								var str = '';
								Ext.each(f.value.split('||'), function(v){
									if(v != null && v != ''){
										if(str == ''){
											str += f.logic + "='" + v + "'";
										} else {
											str += ' OR ' + f.logic + "='" + v + "'";
										}
									}
								});
								if(condition == ''){
									condition += "(" + str + ")";
								} else {
									condition += ' AND (' + str + ")";
								}
							} else if(f.value.toString().charAt(0) == '!'){ 
								if(condition == ''){
									condition += 'nvl(' + f.logic + ",' ')<>'" + f.value.substr(1) + "'";
								} else {
									condition += ' AND (nvl(' + f.logic + ",' ')<>'" + f.value.substr(1) + "')";
								}
							} else {
								if(condition == ''){
									condition += f.logic + "='" + f.value + "'";
								} else {
									condition += ' AND (' + f.logic + "='" + f.value + "')";
								}
							}
						}
					}
				}
			}
			}
		});
		return condition;
    },
    vastDeal: function(url,type){
    	var grid = Ext.getCmp('batchDealGridPanel');       
    	var form = Ext.getCmp('dealform');
    	var wc_id=getUrlParam('keyValue');
    	var panelId=getUrlParam('panelId');
		var records =null;
		if(type&&type=='all'){
			records=Ext.Array.unique(grid.getStore().data.items);
		}else {
			var items = grid.selModel.getSelection();
	        Ext.each(items, function(item, index){
	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        		item.index = this.data[grid.keyField];
	        		grid.multiselected.push(item);
	        	}
	        });
			records = Ext.Array.unique(grid.multiselected);
		}
		if(records.length > 0){
			var params = new Object();
			params.caller = caller;
			var data = new Array();
			var bool = false;
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
			if(bool){
				params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
				params.wc_id=wc_id;
				var main = parent.Ext.getCmp("content-panel");
				main.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + url,
			   		params: params,
			   		method : 'post',
			   		callback : function(options,success,response){
			   			main.getActiveTab().setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				var str = localJson.exceptionInfo;
			   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
			   					str = str.replace('AFTERSUCCESS', '');
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery();
			   				}
			   				showError(str);return;
			   			}
		    			if(localJson.success){
		    				if(localJson.log){
		    					showMessage("提示", localJson.log);
		    				}
			   				Ext.Msg.alert("提示", "处理成功!", function(){
			   					var panel=parent.Ext.getCmp(panelId);
			   					main.getActiveTab().close();
			   					var loadgrid=panel.currentGrid;
			   					loadgrid.getGridColumnsAndStore(grid,'common/singleGridPanel.action',{caller:'WCPlan',condition:'wd_wcid='+wc_id});
			   					main.setActiveTab(panel);
			   				});
			   			}
			   		}
				});
			} else {
				showError("没有需要处理的数据!");
			}
		}
    },
});