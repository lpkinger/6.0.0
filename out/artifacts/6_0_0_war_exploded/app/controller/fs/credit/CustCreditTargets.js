Ext.QuickTips.init();
Ext.define('erp.controller.fs.credit.CustCreditTargets', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:	['fs.credit.CustCreditTargets', 'fs.credit.CustCreditTargetsForm','core.form.Panel', 'core.grid.Panel2', 'core.toolbar.Toolbar',
			'core.trigger.MultiDbfindTrigger', 'core.button.Add','core.button.Submit', 'core.button.Audit', 'core.button.Save',
			'core.button.Close', 'core.button.Print', 'core.button.Upload','core.button.Update', 'core.button.Delete', 'core.button.ResAudit',
			'core.button.ResSubmit', 'core.form.HrefField','core.form.YnField', 'core.form.TimeMinuteField','core.trigger.DbfindTrigger',
			'core.button.Close','core.trigger.TextAreaTrigger', 'core.trigger.DbfindTrigger','core.form.MultiField', 'core.button.Print'
			],
        init:function(){
        	var me = this;
        	this.control({ 
        		'erpGridPanel2': { 
        			beforereconfigure : function(grid,store, columns, oldStore, oldColumns) {
        				Ext.Array.each(columns,function(column){
        						if(column.dataIndex=='cct_ctdetno'){
        							column.flex = 1;
        							column.renderer = function(val, meta, record, x, y, store, view){
        								var detno = record.data['cct_ctdetno'];
        								if(detno.indexOf('.')<0){
		   									meta.style='background-color: #EDEDED!important';
		    	   							return val;
        								}else{   
        									meta.style='background-color: #e0e0e0!important';
		    	   							return val;
        								}
		   							}
        						}else if(column.dataIndex=='cct_ctname'){
        							column.flex = 4;
        							column.renderer = function(val, meta, record, x, y, store, view){
        								var detno = record.data['cct_ctdetno'];
        								if(detno.indexOf('.')<0){
		   									meta.style='background-color: #EDEDED!important';
		    	   							return val;
        								}else{   
        									meta.style='background-color: #e0e0e0!important';
		    	   							return val;
        								}
		   							}
        						}else if(column.dataIndex=='cct_itemvalue'){
        							column.flex = 2;
    								column.editor={
	   									xtype:'combo',
	   									editable:false,
										store: {
										    fields: ['display','value'],
										    data : []
										},
									    queryMode: 'local', 
									    displayField: 'display',
									    valueField: 'value'										
   									}
	   								
	   								column.renderer = function(val, meta, record, x, y, store, view){
	   									var editabled = record.data['ct_editabled'];
	   									var detno = record.data['cct_ctdetno'];	 
	   									if(Math.abs(editabled)==1){	   																	
	   										var combostore = this.down('gridcolumn[dataIndex=cct_itemvalue]').getEditor().store;
	   										var ctid = record.data['cct_ctid'];
	   										me.getComboData(combostore,ctid);
	   										var index = combostore.find('value',val);
	   										if(index<0){
						                        return val;
						                    }
						                    var display = combostore.getAt(index).get('display');
		   									return display;		    	   							
	   									}else if(detno.indexOf('.')<0){
		   									meta.style='background-color: #EDEDED!important';
		    	   							return val;
        								}else{   
        									meta.style='background-color: #e0e0e0!important';
		    	   							return val;
        								}
		   							}        							
							}else{
								column.flex = 2;
								column.renderer = function(val, meta, record, x, y, store, view){
    								var detno = record.data['cct_ctdetno'];
    								if(detno.indexOf('.')<0){
	   									meta.style='background-color: #EDEDED!important';
	    	   							return val;
    								}else{   
    									meta.style='background-color: #e0e0e0!important';
	    	   							return val;
    								}
	   							}
							}								
		   				});
        			},
        			beforeedit : function (editor, e, eOpts) {
						var el = Ext.get(editor.grid.getView().getCell(editor.row, editor.column));
						var ed = editor.column.getEditor(editor.record);	
						if(ed&&(ed.xtype=='combo')){
							var editabled = editor.record.data['ct_editabled'];
	   						if(readOnly||Math.abs(editabled)!=1){	   
								return false;
							}
							var ctid = editor.record.data['cct_ctid'];							
							me.getComboData(ed.store,ctid);
						}								
					}
        		},
        		'erpCustCreditTargetsFormPanel button[id=saveBtn]': {
        			click: function(){
        				me.Save();
        			}
        		},        
        		'erpCustCreditTargetsFormPanel button[id=measureBtn]': {
        			click: function(btn){		
						me.Measure();					
        			}
        		},
        		'erpCustCreditTargetsFormPanel #exportBtn': {
	                click: function(btn) {
	                   	var grid = Ext.getCmp('grid');
	                   	var win = parent.Ext.getCmp('win');
	                   	if(win){
							var title = win.title;
							me.BaseUtil.exportGrid(grid, title);
						}else{
							var title = parent.Ext.getCmp("content-panel").getActiveTab().tabConfig.tooltip;
							me.BaseUtil.exportGrid(grid, title);
						}
	                }
	            }
        	});
        },
        Save:function(){ 
        	var datas = this.GridUtil.getGridStore();   
        	if(datas.length==0){
        		Ext.Msg.alert('警告','未修改数据！');
        		return;
        	}
        	param = "[" + datas.toString() + "]";
			Ext.Ajax.request({
				url:basePath + 'fs/credit/saveCustCreditTargets.action',
				params:{			
					datas:param
				},
				method:'post',
				async: false,
				callback:function(options,success,resp){
					var res = new Ext.decode(resp.responseText);
					if(res.success){
						Ext.Msg.alert('提示','保存成功！',function(){
							window.location.reload();
						});
					}else if(res.exceptionInfo){
						showError(res.exceptionInfo);	
					}
				}
			});
        },
        Measure:function(){   
        	
        	var datas = Ext.getCmp('grid').getStore().data.items;
        	if(datas.length==0){
        		Ext.Msg.alert('警告','没有项目值需要测算！');
        		return;
        	}
        	var bool = true;
        	Ext.Array.each(datas,function(data){
        		if(data.dirty){
        			Ext.Msg.alert('警告','测试得分之前请保存项目值！');
        			bool = false;
        			return;
        		}
        		if((!data.data['cct_itemvalue']||data.data['cct_itemvalue']==""||data.data['cct_itemvalue']==0)&&data.data['ct_editabled']==1){
        			Ext.Msg.alert('警告','测试得分之前请保存项目值！');
        			bool = false;
        			return;
        		}
        	});
        	
        	if(!bool)
        	return;
        	
			Ext.Ajax.request({
				url:basePath + 'fs/credit/MeasureScore.action',
				params:{			
					craid:craid,
					type:type
				},
				method:'post',
				async: false,
				callback:function(options,success,resp){
					var res = new Ext.decode(resp.responseText);
					if(res.success){
						Ext.Msg.alert('提示','测算完成！',function(){
							window.location.reload();
						});						
					}else if(res.exceptionInfo){
						showError(res.exceptionInfo);	
					}
				}
			});
        },
        getComboData: function(store, ctid) {
			if(this._combodata && this._combodata[ctid]) {
				store.loadData(this._combodata[ctid]);
				return;
			}
			var me = this;
			Ext.Ajax.request({
		   		url : basePath + 'common/getFieldsDatas.action',
		   		async: false,
		   		params: {
		   			caller: 'CreditTargetsCombo',
		   			fields: 'ctc_value,ctc_display',
		   			condition: 'ctc_ctid=' + ctid
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);return;
		   			}
	    			if(localJson.success){
	    				var data = Ext.decode(localJson.data), arr = new Array();
	    				for(var i in data) {
	    					arr.push({
	    						display: data[i].CTC_DISPLAY,
	    						value: data[i].CTC_VALUE
	    					});
	    				}
	    				store.loadData(arr);
	    				if(me._combodata == null) {
	    					me._combodata = {};
	    				}
	    				me._combodata[ctid] = arr;
		   			}
		   		}
			});
		}
});