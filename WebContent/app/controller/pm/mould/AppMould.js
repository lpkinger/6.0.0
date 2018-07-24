Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.AppMould', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.mould.AppMould','core.grid.Panel2','core.grid.Panel5','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.button.TurnOffPrice','core.form.FileField','core.button.TurnSale', 'core.button.CreateOtherBill',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'      
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('app_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    			},
    			itemclick: function(selModel, record){
    				if(record.data.ad_id != 0 && record.data.ad_id != null && record.data.ad_id != ''){
    					var btn = Ext.getCmp('updateoffer');
						btn && btn.setDisabled(false);
    				}
    				this.onGridItemClick(selModel, record);
    			}
			},
			'erpGridPanel5': { 
    			itemclick: function(selModel, record){
    				this.onGridItemClick(selModel, record);
    			}
			},
			/**
    		 * 更改是否报价
    		 */
    		'#updateoffer': {
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    me.UpdateOffer(record);
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('app_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('app_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addAppMould', '新增开模申请单', 'jsps/pm/mould/appMould.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('app_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('app_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('app_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('app_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('app_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('app_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('app_statuscode'), app_turnprice = Ext.getCmp('app_turnprice');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(app_turnprice && !Ext.isEmpty(app_turnprice.value)){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('app_id').value);
    			}
    		},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('app_id').value);
				}
			},
			'erpTurnSaleButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('app_statuscode'), turnstatus = Ext.getCmp('app_turnsalecode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value == 'TURNSA'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入模销售单吗?", function(btn){
    					if(btn == 'yes'){
    						var id = Ext.getCmp('app_id').value;
    						me.FormUtil.setLoading(true);
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/turnMouldSale.action',
    	    			   		params: {
    	    			   			id: id
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
						   			me.FormUtil.setLoading(false);
						   			var localJson = new Ext.decode(response.responseText);
						   			if(localJson.exceptionInfo){
						   				showError(localJson.exceptionInfo);
						   				return "";
						   			}
					    			if(localJson.success){
					    				if(localJson.log){
					    					showMessage("提示", localJson.log);
					    				}
					    				window.location.reload();
						   			}
			   					}
    	    				});
    					}
    				});
    			}
			},
			'erpTurnOffPriceButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('app_statuscode'), turnstatus = Ext.getCmp('app_turnpricecode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value == 'TURNPM'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.batchdeal('AppMould!toPrice', ' ad_appid=' + Ext.getCmp('app_id').value + ' and nvl(ad_statuscode,\' \')<>\'TURNPM\' and nvl(ad_closestatuscode,\' \')<>\'FINISH\'', 'pm/mould/turnPriceMould.action');
    			}
			},
			'erpCreateOtherBillButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('app_statuscode'), iscust = Ext.getCmp('app_iscust'),
                        app_payamount = Ext.getCmp('app_statuscode');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
                    if (app_payamount && Ext.isEmpty(app_payamount.value) || app_payamount.value == '0') {
                        btn.hide();
                    }
                    if (iscust && Ext.isEmpty(iscust.value) || iscust.value == '0') {
                        btn.hide();
                    }
                },
                click: function(btn) {
                    Ext.Ajax.request({
                        url: basePath + 'pm/mould/turnOtherBill.action',
                        params: {
                            caller: caller,
                            id: Ext.getCmp('app_id').value
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                            var res = new Ext.decode(response.responseText);
                            if (res) {
                                if (res.exceptionInfo) {
                                    showMessage('提示', res.exceptionInfo);
                                } else {
                                	showMessage('提示', '产生其它应收单成功');
                                }
                            } else {
                                showMessage('提示', '产生其它应收单失败');
                            }

                        }
                    });
                }
            },
			'dbfindtrigger[name=ad_pscode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('app_prjcode')){
    					var code = Ext.getCmp('app_prjcode').value;
    					if(code != null && code != ''){
    						var obj = me.getCodeCondition();
        					if(obj && obj.field){
        						t.dbBaseCondition = obj.field + "='" + code + "'";
        					}
    					}
    				}
    			},
    			aftertrigger: function(t){
    				if(Ext.getCmp('app_prjcode')){
    					var obj = me.getCodeCondition();
    					if(obj && obj.fields){
    						me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
    					}
    				}
    			}
    		}
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getCodeCondition: function(){
		var field = "ps_prjcode";
		var tFields = 'app_prjcode,app_prjname,prj_sptext70,prj_assignto';
		var fields = 'ps_prjcode,ps_prjname,ps_description,ps_assignto';
		var tablename = 'ProductSet';
		var myfield = 'ps_code';
		var obj = new Object();
		obj.field = field;
		obj.fields = fields;
		obj.tFields = tFields;
		obj.tablename = tablename;
		obj.myfield = myfield;
		return obj;
	},
	UpdateOffer:function(record){
		var win = this.window;
		if (!win) {
			win = this.window = this.getWindow(record);
		}
		win.show();
	},
	getWindow : function(record) {
		var me = this;
		return Ext.create('Ext.window.Window',{
			width: 330,
	       	height: 180,
	       	closeAction: 'hide',
	       	cls: 'custom-blue',
	       	title:'<h1>更改是否报价</h1>',
	       	layout: {
	       		type: 'vbox'
	       	},
	       	items:[{
				margin: '5 0 0 5',
				xtype: 'erpYnField',
				fieldLabel: '是否报价',
				id:'isoffer',
				name: 'isoffer',
				readOnly:false,
				allowBlank: false,
				value : record.data.ad_isoffer
			}],
	       	 buttonAlign:'center',
	       	 buttons:[{
	 				xtype:'button',
	 				text:'保存',
	 				width:60,
	 				iconCls: 'x-button-icon-save',
	 				handler:function(btn){
	 					var w = btn.up('window');
	 					me.saveOffer(w);
	 					w.hide();
	 				}
	 			},{
	 				xtype:'button',
	 				columnWidth:0.1,
	 				text:'关闭',
	 				width:60,
	 				iconCls: 'x-button-icon-close',
	 				margin:'0 0 0 10',
	 				handler:function(btn){
	 					btn.up('window').hide();
	 				}
	 			}]
	        });
	},
	saveOffer: function(w) {
		var isoffer = w.down('#isoffer').getValue(),
			grid = Ext.getCmp('grid'),
			app_id = Ext.getCmp('app_id').value,
			record = grid.getSelectionModel().getLastSelected(); 
		if(!isoffer) {
			showError('请先选择是否报价.') ;  
			return;
		} else {
			var dd = {
					ad_id : record.data.ad_id,
					ad_appid : app_id,
					isoffer : isoffer ? isoffer : 1
			};
			Ext.Ajax.request({
				url : basePath +'pm/mould/updateIsOffer.action',
				params : {
					_noc: 1,
					data: unescape(Ext.JSON.encode(dd))
				},
				method : 'post',
				callback : function(opt, s, res){
					var r = new Ext.decode(res.responseText);
					if(r.success){
	    				showMessage('提示', '更新成功!', 1000);
	    				grid.GridUtil.loadNewStore(grid, {caller: 'AppMould', condition: 'ad_appid=' + app_id});
		   			} else if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			} else{
		   				saveFailure();
		   			}
				}
			});
		}
	},
	batchdeal: function(nCaller, condition, url) {
        var win = new Ext.window.Window({
            id: 'win',
            height: "100%",
            width: "80%",
            maximizable: true,
            buttonAlign: 'center',
            layout: 'anchor',
            items: [{
                tag: 'iframe',
                frame: true,
                anchor: '100% 100%',
                layout: 'fit',
                html: '<iframe id="iframe_' + caller + '" src="' + basePath + 'jsps/common/editorColumn.jsp?caller=' + nCaller +
                    "&condition=" + condition + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
            }],
            buttons: [{
                name: 'confirm',
                text: $I18N.common.button.erpConfirmButton,
                iconCls: 'x-button-icon-confirm',
                cls: 'x-btn-gray',
                listeners: {
                    buffer: 500,
                    click: function(btn) {
                        var grid = Ext.getCmp('win').items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow.Ext.getCmp("editorColumnGridPanel");
                        btn.setDisabled(true);
                        grid.updateAction(url);
                    }
                }
            }, {
                text: $I18N.common.button.erpCloseButton,
                iconCls: 'x-button-icon-close',
                cls: 'x-btn-gray',
                handler: function() {
                    Ext.getCmp('win').close();
                }
            }]
        });
        win.show();
    }
});