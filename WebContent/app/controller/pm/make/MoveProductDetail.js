Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MoveProductDetail', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.make.MoveProductDetail','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.Flow','core.button.Get'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					} 
    				var bool = true; 
    				if(bool){
    					this.FormUtil.beforeSave(this);
    				}
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('mp_id').value);
				}
			},
			'erpGetButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('mp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					Ext.Ajax.request({
						url: basePath + 'pm/make/moveProduct.action',
						params: {
							formStore: unescape(Ext.JSON.encode(btn.ownerCt.ownerCt.getValues()).replace(/\\/g,"%"))
						},
						callback: function(opt, s, r) {
							var rs = Ext.decode(r.responseText);
							if(rs.exceptionInfo) {
								showError(rs.exceptionInfo);
							} else if(rs.id && rs.id > 0) {
								var a = window.location.href;
								window.location.href = a.substr(0, a.indexOf('.jsp') + 4) + '?formCondition=mp_id=' + rs.id + 
									'&gridCondition=mpd_mpid=' + rs.id;
							}
						}
					});
				}
			},
			'erpUpdateButton': {
				click: function(btn){ 
    				var bool = true; 
    				if(bool){
    					this.FormUtil.onUpdate(this);
    				}
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addMoveProductDetail', '新增制造挪料单维护', 'jsps/pm/make/moveProductDetail.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('mp_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('mp_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('mp_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('mp_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('mp_id').value);
				}
			},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('mp_id').value);
				}
			},
			'dbfindtrigger[name=mpd_fromdetno]': {
    			afterrender: function(t){
    				t.gridKey = "mp_frommakecode";
    				t.mappinggirdKey = "mm_code";
    				t.gridErrorMessage = "请先选择制造单!";
    			}
    		},
    		'dbfindtrigger[name=mpd_todetno]': {
    			afterrender: function(t){
    				t.gridKey = "mp_tomakecode";
    				t.mappinggirdKey = "mm_code";
    				t.gridErrorMessage = "请先选择制造单!";
    			}
    		}
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
	    this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});