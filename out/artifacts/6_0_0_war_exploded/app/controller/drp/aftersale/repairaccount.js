Ext.QuickTips.init();
Ext.define('erp.controller.drp.aftersale.repairaccount', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','drp.aftersale.repairaccount','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      			'core.button.ResSubmit','core.button.TurnStorage','core.button.TurnCheck','core.form.FileField',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger','core.button.VastMakeBill'
      	],
    init:function(){
    	var me = this;
    	me.allowinsert = true;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ra_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addrepairaccount', '新增维修结算单', 'jsps/drp/aftersale/repairaccount.jsp?whoami=RepairAccount');
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ra_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ra_id').value);
				}
			},'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ra_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ra_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ra_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ra_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ra_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ra_id').value);
				}
			},
            'erpVastMakeBillButton': {
                click: function(btn) {
                    var ra_id = Ext.getCmp("ra_id").rawValue;
                    Ext.Ajax.request({
                        url : basePath + 'drp/aftersale/makeBill.action',
                        params : {
                            ra_id: ra_id
//                            formStore: Ext.getCmp('form').getValues(),
//                            gridStore:  me.GridUtil.getGridStore(me)
//                       //     gridStore: Ext.getCmp('grid').getGridStore()
                        },
                        method : 'post',
                        callback : function(options,success,response){
                            var ret = new Ext.decode(response.responseText);
                             if (ret.success) {
                                Ext.Msg.alert("提示", "开票成功!");
                             } else if (ret.exceptionInfo) {
                                Ext.Msg.alert('提示', ret.exceptionInfo);
                             } else {
                                Ext.Msg.alert('提示', '开票失败!');
                             }
                        }
                    });
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

	beforeSave: function(){
		var bool = true;
//		var grid = Ext.getCmp('grid');
//		var items = grid.store.data.items, whcode = Ext.getCmp('va_whcode').value;
//		Ext.each(items, function(item){
//			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
//				if(item.data['vad_whcode'] == null){
//					item.set('vad_whcode', whcode);
//				}
//				if(item.data['vad_qty'] == null || item.data['vad_qty'] == '' ||
//						item.data['vad_qty'] == '0' || item.data['vad_qty'] == 0){
//					bool = false;
//					showError('明细表第' + item.data['vad_detno'] + '行的数量为空或0');return;
//				}
//			}
//		});
		if (bool) {
		    this.FormUtil.beforeSave(this);
		}
	},
	beforeUpdate: function(){
		var bool = true;
//		var grid = Ext.getCmp('grid');
//		var items = grid.store.data.items, whcode = Ext.getCmp('va_whcode').value;
//		Ext.each(items, function(item){
//			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
//				if(item.data['vad_whcode'] == null){
//					item.set('vad_whcode', whcode);
//				}
//				if(item.data['vad_qty'] == null || item.data['vad_qty'] == '' ||
//						item.data['vad_qty'] == '0' || item.data['vad_qty'] == 0){
//					bool = false;
//					showError('明细表第' + item.data['vad_detno'] + '行的数量为空或0');return;
//				}
//			}
//		});
		if (bool) {
			this.FormUtil.onUpdate(this);
		}
	}
});