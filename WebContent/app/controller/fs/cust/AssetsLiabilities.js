Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.AssetsLiabilities', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.AssetsLiabilities', 'core.grid.Panel2',
			'core.button.Save', 'core.button.Upload','core.button.Close','core.button.Delete',
			'core.button.Update','core.button.Export',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
			'core.form.StatusField','core.form.FileField','core.form.MultiField'],
	init : function() {
		var me = this;
		this.control({
			'field[name=al_id]': {
				afterrender:function(field){
					if(formCondition){
						var id = formCondition.substring(formCondition.indexOf('=')+1);
						field.setValue(id);
					}
				}
			},
			'erpSaveButton': {
				afterrender:function(btn){
					if(readOnly==1){
						btn.hide();
					}
				},
		    	click: function(btn){
		    		me.beforeSave();	
		    	}
		    },
		    '#al_accountinforar': {
    			itemclick: function(view, record) {
    				var ai_id = record.get('ai_id');
    				var ai_alid = record.get('ai_alid');
    				var ai_cushortname = record.get('ai_cushortname');
    				if(ai_id&&ai_alid){
    					me.loadDetail('AL_AccountInforARDet', ai_id,ai_alid,ai_cushortname,'应收账款');
    				}
    			}
    		},
    		'#al_accountinforap': {
    			itemclick: function(view, record) {
    				var ai_id = record.get('ai_id');
    				var ai_alid = record.get('ai_alid');
    				var ai_cushortname = record.get('ai_cushortname');
    				if(ai_id&&ai_alid){
    					me.loadDetail('AL_AccountInforAPDet', ai_id,ai_alid,ai_cushortname,'应付账款');
    				}
    			}
    		}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	loadDetail: function(nCaller,ai_id,ai_alid,ai_cushortname,kind) {
		var me = this;
		var condition = ' aid_aiid=' + ai_id,title;
		if(nCaller=='AL_AccountInforARDet'){
			title = '欠款明细';
		}else{
			title = '债权人明细';
		}
		var win = new Ext.window.Window({
            id: 'win',
            height: "90%",
            width: "80%",
            title:title,
            maximizable: true,
            buttonAlign: 'center',
           layout: 'fit',
            items:[{
				xtype : 'erpGridPanel2',
				id: 'al_accountinforDet',
				caller:nCaller,
				condition:condition,
				keyField : 'aid_id',
				mainField : 'aid_alid',
				listeners: {
					reconfigure : function(grid,store, columns, oldStore, oldColumns){
						store.on('datachanged',function(store,records){
							for(var i=0;i<records.length;i++){
								var record = records[i];
								var bool = true;
								if(record.data['aid_aiid']&&record.data['aid_alid']&&record.data['ai_cushortname']&&record.data['aid_kind']){
									bool = false;
								}
								if(bool){
									record.set('aid_aiid',ai_id);
									record.set('aid_alid',ai_alid);
									record.set('ai_cushortname',ai_cushortname);
									record.set('aid_kind',kind);
								}
							}
						});
						
					},
					itemclick: function(selModel, record) {
	    				me.onGridItemClick(selModel, record);
	    			}
				},
				tbar:['->',{
					xtype:'button',
					id:'saveBtn',
					text: $I18N.common.button.erpSaveButton,
					iconCls: 'x-button-icon-save',
			    	cls: 'x-btn-gray',
			    	width: 60,
			    	style: {
			    		marginLeft: '10px'
			        },
					listeners: {
						click:function(btn){
							var grid = btn.ownerCt.ownerCt;
		    				var data = me.getGridStore(grid);
		    				if(data != null) {
	    						grid.setLoading(true);
	            				Ext.Ajax.request({
	            		        	url : basePath + 'fs/cust/saveAccountInforDet.action',
	            		        	params: {
	            		        		gridStore: "[" + data.toString() + "]"
	            		        	},
	            		        	method : 'post',
	            		        	callback : function(options,success,response){
	            		        		grid.setLoading(false);
	            		        		var res = new Ext.decode(response.responseText);
	            		        		if(res.exceptionInfo){
	            		        			showError(res.exceptionInfo);return;
	            		        		}
	            		        		if(res.success){
	            		        			saveSuccess(function(){
	            		        				me.GridUtil.loadNewStore(grid,{'caller':nCaller,'condition':condition});
	            		        			});
	            		        		};
	            		        	}
	            		        });
	    					}
	    				}
					}
				},{
					xtype:'erpCloseButton',
					listeners: {
						click:function(btn){
							var win = btn.ownerCt.ownerCt.ownerCt;
							win.close();
						}
					}
				},'->']
            }]
        });
        win.show();
    },
	beforeSave:function(isUpdate){
		var me = this;
		var form = Ext.getCmp('form');
		
		var grid1 = Ext.getCmp('al_accountinforar');//主要客户应收账款
		var grid2 = Ext.getCmp('al_accountinforlong');//长期借款
		var grid3 = Ext.getCmp('al_accountinforothar');//其他应收账款
		var grid4 = Ext.getCmp('al_accountinforpp');//预付账款
		var grid5 = Ext.getCmp('al_accountinforinv');//存货
		var grid6 = Ext.getCmp('al_accountinforfix');//固定资产
		var grid7 = Ext.getCmp('al_accountinforcb');//短期借款-授信银行
		var grid8 = Ext.getCmp('al_accountinforlb');//短期借款-贷款银行
		var grid9 = Ext.getCmp('al_accountinforap');//应付账款-主要债权人
		var grid10 = Ext.getCmp('al_accountinforothap');//其他应付账款
		
		var param1 = new Array();
		if(grid1){
			param1 = me.GridUtil.getGridStore(grid1);
		}
		var param2 = new Array();
		if(grid2){
			param2 = me.GridUtil.getGridStore(grid2);
		}
		var param3 = new Array();
		if(grid3){
			param3 = me.GridUtil.getGridStore(grid3);
		}
		var param4 = new Array();
		if(grid4){
			param4 = me.GridUtil.getGridStore(grid4);
		}
		var param5 = new Array();
		if(grid5){
			param5 = me.GridUtil.getGridStore(grid5);
		}
		var param6 = new Array();
		if(grid6){
			param6 = me.GridUtil.getGridStore(grid6);
		}
		var param7 = new Array();
		if(grid7){
			param7 = me.GridUtil.getGridStore(grid7);
		}
		var param8 = new Array();
		if(grid8){
			param8 = me.GridUtil.getGridStore(grid8);
		}
		var param9 = new Array();
		if(grid9){
			param9 = me.GridUtil.getGridStore(grid9);
		}
		var param10 = new Array();
		if(grid10){
			param10 = me.GridUtil.getGridStore(grid10);
		}
		
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
		param4 = param4 == null ? [] : "[" + param4.toString().replace(/\\/g,"%") + "]";
		param5 = param5 == null ? [] : "[" + param5.toString().replace(/\\/g,"%") + "]";
		param6 = param6 == null ? [] : "[" + param6.toString().replace(/\\/g,"%") + "]";
		param7 = param7 == null ? [] : "[" + param7.toString().replace(/\\/g,"%") + "]";
		param8 = param8 == null ? [] : "[" + param8.toString().replace(/\\/g,"%") + "]";
		param9 = param9 == null ? [] : "[" + param9.toString().replace(/\\/g,"%") + "]";
		param10 = param10 == null ? [] : "[" + param10.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
			});
			me.save(r, param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, isUpdate);
		} else{
			me.FormUtil.checkForm();
		}		
	},
	save: function(){
		var me = this;
		var form = Ext.getCmp('form');
		var params = new Object();
		var r = arguments[0],isUpdate = arguments[arguments.length-1];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});	
		params.caller = caller;
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param1 = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		params.param3 = unescape(arguments[3].toString().replace(/\\/g,"%"));
		params.param4 = unescape(arguments[4].toString().replace(/\\/g,"%"));
		params.param5 = unescape(arguments[5].toString().replace(/\\/g,"%"));
		params.param6 = unescape(arguments[6].toString().replace(/\\/g,"%"));
		params.param7 = unescape(arguments[7].toString().replace(/\\/g,"%"));
		params.param8 = unescape(arguments[8].toString().replace(/\\/g,"%"));
		params.param9 = unescape(arguments[9].toString().replace(/\\/g,"%"));
		params.param10 = unescape(arguments[10].toString().replace(/\\/g,"%"));
		Ext.Ajax.request({
			url : basePath + form.saveUrl,
			params : params,
			method : 'post',
			callback : function(options,success,response){	   			
			   	var localJson = new Ext.decode(response.responseText);
		    	if(localJson.success){
    				showMessage('提示', '保存成功!', 1000);
    				window.location.reload();
	   			} else if(localJson.exceptionInfo){
   					showError(localJson.exceptionInfo);
	   				return;
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
			}
		});
	},
	getGridStore: function(grid){
		var msg = this.GridUtil.checkGridDirty(grid);
		if(msg == '') {
			showMessage('警告', '没有新增或修改数据.');
			return null;
		} else {
			return this.GridUtil.getGridStore(grid);
		}
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    }
});