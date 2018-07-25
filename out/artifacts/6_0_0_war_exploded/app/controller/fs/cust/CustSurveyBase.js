Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.CustSurveyBase', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.CustSurveyBase', 'core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField','core.button.Save',
			'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit','core.button.Audit','core.button.Close',
			'core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit', 'core.button.Export',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn', 'core.form.StatusField',
			'core.form.FileField','core.button.CopyAll','core.button.ResetSync', 'core.button.RefreshSync'],
	init : function() {
		var me = this;
		this.control({
			'field[name=bs_caid]': {
				afterrender:function(field){
					if(formCondition){
						var id = formCondition.substring(formCondition.indexOf('=')+1);
						field.setValue(id);
					}
				}
			},
			'field[name=bs_organization]': {
    			beforerender : function(f) {
    				f.emptyText = '简要介绍公司历史沿革、主要股东及列示股权架构图';
				}
    		},
    		'field[name=bs_checks]': {
    			beforerender : function(f) {
    				f.emptyText = '请核实上述企业（含股东、子公司、关联企业）是否为逃废债企业';
				}
    		},
			'#mfcust': { 
				itemclick: function(selModel, record){
					if (record.data.mf_id != 0 && record.data.mf_id != null && record.data.mf_id != '') {
    					var btn = Ext.getCmp('updatemfcustinfodet');
    					btn && btn.setDisabled(false);
    				}
					this.onGridItemClick(selModel, record);
    			}
    		},
		     '#updatemfcustinfodet': {
		    	//双方交易情况维护
			    click: function(btn) {
		        	var grid = Ext.getCmp('mfcust'), record = grid.getSelectionModel().getLastSelected();
			    	var mf_id = record.get('mf_id'), mf_cqid = record.get('mf_cqid');
			    	if(mf_id && mf_id){
			    		me.loadDetail(mf_id, mf_cqid);
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
        	}
		})
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	beforeSave:function(){
		var me = this;
		var form = Ext.getCmp('form');
		var grid1 = Ext.getCmp('mfcust');	
		var grid2 = Ext.getCmp('inverstment');
		var grid3 = Ext.getCmp('relation');
		var grid4 = Ext.getCmp('guarantee');
		
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
		
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		param3 = param3 == null ? [] : "[" + param3.toString().replace(/\\/g,"%") + "]";
		param4 = param4 == null ? [] : "[" + param4.toString().replace(/\\/g,"%") + "]";
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
			me.save(r, param1, param2, param3, param4);
		}else{
			me.FormUtil.checkForm();
		}		
	},
	save: function(){
		var me = this;
		var form = Ext.getCmp('form');
		var params = new Object();
		var r = arguments[0];
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
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
	   		url : basePath + 'fs/cust/saveSurveyBase.action?_noc=1',
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.setLoading(false);
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
	loadDetail: function(mf_id, mf_cqid) {
		var me = this, nCaller = 'FSMFCustInfo';
		var condition = ' mfd_mfid=' + mf_id;
		var win = new Ext.window.Window({
            height: "80%",
            width: "80%",
            title: "近三年双方交易情况",
            maximizable: true,
            buttonAlign: 'center',
            layout: 'fit',
            closeAction: 'destroy',
            items:[{
				xtype : 'erpGridPanel2',
				id: 'mfcustinfodet',
				bbar: {xtype: 'toolbar'},
				caller:nCaller,
				condition:condition,
				keyField : 'mfd_id',
				listeners: {
					reconfigure : function(grid,store, columns, oldStore, oldColumns){
						store.on('datachanged',function(store,records){
							for(var i=0;i<records.length;i++){
								var record = records[i];
								var bool = true;
								if(record.data['mfd_mfid']&&record.data['mfd_cqid']){
									bool = false;
								}
								if(bool){
									record.set('mfd_mfid', mf_id);
									record.set('mfd_cqid', mf_cqid);
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
					text: $I18N.common.button.erpSaveButton,
					iconCls: 'x-button-icon-save',
			    	cls: 'x-btn-gray',
			    	width: 60,
			    	style: {
			    		marginLeft: '10px'
			        },
					listeners: {
						afterrender:function(btn){
							if(readOnly==1){
								btn.hide();
							}
						},
						click:function(btn){
							var grid = Ext.getCmp('mfcustinfodet');
		    				var data = me.getGridStore(grid);
		    				if(data != null) {
	    						grid.setLoading(true);
	            				Ext.Ajax.request({
	            		        	url : basePath + 'fs/cust/saveFSMFCustInfoDet.action?_noc=1',
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
	getGridStore: function(grid){
		var msg = this.GridUtil.checkGridDirty(grid);
		if(msg == '') {
			showMessage('警告', '没有新增或修改数据.');
			return null;
		} else {
			return this.GridUtil.getGridStore(grid);
		}
	}
});