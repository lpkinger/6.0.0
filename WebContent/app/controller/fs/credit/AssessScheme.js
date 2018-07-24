Ext.QuickTips.init();
Ext.define('erp.controller.fs.credit.AssessScheme', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fs.credit.AssessScheme','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.form.HrefField',
			'core.form.YnField','core.form.TimeMinuteField','core.trigger.DbfindTrigger','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.MultiField'
    	],
        init:function(){
        	var me = this;
        	this.control({ 
        		'erpGridPanel2': { 
        			beforereconfigure : function(grid,store, columns, oldStore, oldColumns) {								
						Ext.Array.each(columns,function(column){	
						 	if(column.dataIndex=='asd_satisfaction'||column.dataIndex=='asd_nsatisfaction'||column.dataIndex=='asd_standard'){	
   								column.renderer = function(val, meta, record, x, y, store, view){
   									var data = record.data;
   								
   									if(data['asd_ctid']&&data['asd_ctisleaf']&&data['asd_ctisleaf']=='0'){
   										meta.style = "background: #e0e0e0;";
   										meta.tdCls="tdcss"; 
   									}		   									
    	   							return val;
								}
	   						}
	   					});
        			},
        			itemclick: function(selModel, record){								
    					this.onGridItemClick(selModel, record);   					
        			},
        			beforeedit : function (editor, e, eOpts) {        
						var ed = editor.column.getEditor(editor.record);											
						if(ed&&(ed.name=='asd_satisfaction'||ed.name=='asd_nsatisfaction'||ed.name=='asd_standard')){
							var data = editor.record.data;				
							if(data['asd_ctisleaf']&&data['asd_ctisleaf']=='0'){
								return false;
							}
						}
					}
        		},
        		'dbfindtrigger[name=asd_ctname]':{   
        			aftertrigger:function(t,record,dbfinds){   	   		  
    		   			var grid = t.owner;    
    		   			var store = grid.store;
    		   			var count =0;
    		   			if(record){
    		   				Ext.Array.each(store.data.items,function(item){    		   			
    		   					if(item.data['asd_ctid']&&record.data['ct_id']==item.data['asd_ctid']){  
    		   						count++;
    		   						if(count>1&&item.dirty){
    		   							showError('指标不能重复！');    		   							
    		   							store.remove(item);
    		   							return false;
    		   						}
    		   						
    		   					}
    		   				});	    		   	
    		   			}
    		   		}
        		},
        		'erpAddButton': {
        			click: function(){
        				me.FormUtil.onAdd('AssessScheme', '评估方案设置', 'jsps/fs/credit/AssessScheme.jsp');
        			}
        		},        
        		'erpSaveButton': {
        			click: function(btn){
        				var as_finance = Ext.getCmp('as_finance');
        				var as_nofinance = Ext.getCmp('as_nofinance');
        				if(as_finance && as_finance.value > 100){
        					showError('财务占比不能大于100！');
        					return;
        				}
        				if(as_nofinance && as_nofinance.value > 100){
        					showError('非财务占比不能大于100！');
        					return;
        				}
						this.FormUtil.beforeSave(this);
        			}
        		},
        		'erpCloseButton': {
        			click: function(btn){
        				this.FormUtil.beforeClose(this);
        			}
        		},
        		'erpUpdateButton': {
        			click: function(btn){
        				var as_finance = Ext.getCmp('as_finance');
        				var as_nofinance = Ext.getCmp('as_nofinance');
        				if(as_finance && as_finance.value > 100){
        					showError('财务占比不能大于100！');
        					return;
        				}
        				if(as_nofinance && as_nofinance.value > 100){
        					showError('非财务占比不能大于100！');
        					return;
        				}
        				this.FormUtil.onUpdate(this);	
        			}
        		},
        		'erpDeleteButton': {
        			click: function(btn){
        				me.FormUtil.onDelete((Ext.getCmp('as_id').value));
        			}
        		},
          		'erpSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('as_statuscode');
        				if(status && status.value != 'ENTERING'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				if(me.checkGrid()){
        					me.FormUtil.onSubmit(Ext.getCmp('as_id').value);
        				}
        			}
        		},
        		'erpResSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('as_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onResSubmit(Ext.getCmp('as_id').value);
        			}
        		},
        		'erpAuditButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('as_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onAudit(Ext.getCmp('as_id').value);
        			}
        		},
        		'erpResAuditButton' : {
					afterrender : function(btn) {
						var status = Ext.getCmp('as_statuscode');
						if (status && status.value != 'AUDITED') {
							btn.hide();
						}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('as_id').value);
				}
			}		
        	});
        },
        onGridItemClick: function(selModel, record){//grid行选择
        	this.GridUtil.onGridItemClick(selModel, record); 	
        },
    	checkGrid: function(){
    		var grid = Ext.getCmp('grid'), items = grid.store.data.items;
			var bool = true;
			// 计划完成日期不能小于计划开始日期
			Ext.each(items, function(item) {
				if (item.dirty
						&& item.data[grid.necessaryField] != null
						&& item.data[grid.necessaryField] != "") {
					if(item.data['asd_type']&&item.data['asd_type']=='FINANCE'&&item.data['asd_ctisleaf']&&Math.abs(parseInt(item.data['asd_ctisleaf']))==1){
						if(!item.data['asd_assesssql']||item.data['asd_assesssql']==''){
							showError('类型为财务因素的子级项目，评分SQL不能为空！');
							bool = false;
							return false;
						}						
					}
				}
			});
			return bool;
        }
});