Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.CustomerQuota', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.CustomerQuota', 'core.grid.Panel2','core.toolbar.Toolbar',
			'core.button.Save', 'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit',
			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
			'core.button.Export', 'core.button.MFCust','core.button.Sync','core.button.FormsDoc', 'core.form.SeparNumber',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
			'core.form.StatusField','core.form.FileField','core.form.MultiField','core.button.InfoPerfect','core.button.PrintByCondition'],
	init : function() {
		var me = this;
		this.control({
			'erpFormPanel' : {
    			afterload : function(form) {
    				this.hidecolumns(form.down('#cq_quotatype'));
				}
    		},
    		'combo[name=cq_quotatype]': {
    			delay: 200,
    			change: function(m){
					this.hidecolumns(m);
				}
    		},
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('CustomerQuota', '客户保理额度', 'jsps/fs/cust/customerQuota.jsp');
    			}
        	},
        	'erpInfoPerfectButton': {
				click : function(btn) {
					var cq_id = Ext.getCmp('cq_id'),caid = null;
					if(cq_id&&cq_id.value){
						cqid = cq_id.value;
					}
					var readOnly = 1;
					var status = Ext.getCmp('cq_statuscode');
					if (status && status.value == 'ENTERING') {
						readOnly = 0;
					}
					var cq_custname = Ext.getCmp('cq_custname'),custname = '';
					if(cq_custname&&cq_custname.value){
						custname = cq_custname.value;
					}
					me.FormUtil.setLoading(true);
					Ext.Ajax.request({
				   		url : basePath + 'fs/cust/getDefaultDatas.action',
				   		params : {cqid:cqid},
				   		method : 'post',
				   		callback : function(options,success,response){	
				   			me.FormUtil.setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
			    			if(localJson.exceptionInfo){
				   				str = localJson.exceptionInfo;
			   					showError(str);
				   			}
				   			me.FormUtil.onAdd('InfoPerfect'+cqid, custname+'项目风控报告', 'jsps/fs/cust/infoPerfect.jsp?caid='+cqid+'&readOnly='+readOnly+'&custname='+custname);
				   		}
					});
				}
        	},
        	'erpMFCustButton': {
        		afterrender : function(btn) {
					var cq_quotatype = Ext.getCmp('cq_quotatype').value;
					if(cq_quotatype != '正向保理业务'){
						btn.hide();
					}
				},
				click : function(btn) {
					var cq_id = Ext.getCmp('cq_id').value, caid = null;
					var readOnly = false;
					var status = Ext.getCmp('cq_statuscode');
					if (status && status.value != 'ENTERING') {
						readOnly = true;
					}
					me.loadDetail(cq_id, readOnly);
				}
        	},
			'erpSaveButton': {
    			click: function(btn){
    				var cq_feetype = Ext.getCmp('cq_maxperiod').value;
    				if(cq_feetype > 30){
    					showError('最长宽限期天数不能超过30天！');
						return;
    				}
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
					this.FormUtil.beforeSave(this);				
    			}
        	},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('cq_id').value);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					var cq_feetype = Ext.getCmp('cq_maxperiod').value;
    				if(cq_feetype > 30){
    					showError('最长宽限期天数不能超过30天！');
						return;
    				}
					me.FormUtil.onUpdate(this);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cq_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					var cq_feetype = Ext.getCmp('cq_maxperiod').value;
    				if(cq_feetype > 30){
    					showError('最长宽限期天数不能超过30天！');
						return;
    				}
					me.FormUtil.onSubmit(Ext.getCmp('cq_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cq_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('cq_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cq_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('cq_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cq_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('cq_id').value);
				}
			},
			'erpSyncButton':{
    			afterrender: function(btn){
					var status = Ext.getCmp('cq_statuscode');
					if(status && status.value != 'AUDITED' && status.value != 'BANNED' && status.value != 'DISABLE'){
						btn.hide();
					}
				}
    		}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	hidecolumns:function(m){
		if(!Ext.isEmpty(m.getValue())) {
			var form = m.ownerCt;
			Ext.getCmp('cq_factorcode') && Ext.getCmp('cq_factorcode').hide();
			Ext.getCmp('cq_factorname') && Ext.getCmp('cq_factorname').hide();
			if(m.value == '反向保理业务'){
				form.down('#cq_hxcustcode').show();
				form.down('#cq_hxcustname').show();
				form.down('#cq_cacode').show();
				form.down('#cq_hxcredit').show();
			} else {
				form.down('#cq_hxcustcode').hide();
				form.down('#cq_hxcustname').hide();
				form.down('#cq_cacode').hide();
				form.down('#cq_hxcredit').hide();
				Ext.getCmp('erpMFCustButton') && Ext.getCmp('erpMFCustButton').show();
			}
			if (m.value == '票据再保理'){
				Ext.getCmp('cq_factorcode') && Ext.getCmp('cq_factorcode').show();
				Ext.getCmp('cq_factorname') && Ext.getCmp('cq_factorname').show();
			}
		}
	},
	loadDetail: function(cq_id, readOnly) {
		var me = this;
		var nCaller = 'CustomerQuota!MFCust', condition = ' mf_cqid=' + cq_id;
		var win = new Ext.window.Window({
			id: 'mfcust',
            height: "85%",
            width: "70%",
            title: '买方客户维护',
            maximizable: true,
            buttonAlign: 'center',
            layout: 'fit',
            closeAction: 'destroy',
            items:[{
				xtype : 'erpGridPanel2',
				caller: nCaller,
				condition: condition,
				keyField : 'mf_id',
				mainField : 'mf_cqid',
				readOnly: readOnly,
				listeners: {
					reconfigure : function(grid,store, columns, oldStore, oldColumns){
						store.on('datachanged',function(store,records){
							for(var i=0;i<records.length;i++){
								var record = records[i];
								record.set('mf_cqid',cq_id);
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
						click:function(btn){
							var grid = btn.ownerCt.ownerCt;
		    				var data = me.getGridStore(grid);
		    				if(data != null) {
	    						grid.setLoading(true);
	            				Ext.Ajax.request({
	            		        	url : basePath + 'fs/cust/saveMFCustInfo.action',
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
					xtype: 'button',
					text: $I18N.common.button.erpCloseButton,
					iconCls : 'x-button-icon-close',
					cls : 'x-btn-gray',
					width : 65,
					listeners: {
						click:function(btn){
							Ext.getCmp('mfcust').close();
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
	},
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    }
});