Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.MouldQuote', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.mould.MouldQuote','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.form.YnField'      
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				afterrender: function(grid){
    				var status = Ext.getCmp('mq_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    			},
    			itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				me.getprice();
    				this.FormUtil.beforeSave(this);
    			}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('mq_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('mq_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.getprice();
    				this.FormUtil.onUpdate(this);
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addMouldQuote', '新增模具报价单', 'jsps/pm/mould/mouldQuote.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mq_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('mq_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mq_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('mq_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mq_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('mq_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mq_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('mq_id').value);
    			}
    		},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('mq_id').value);
				}
			},
			'dbfindtrigger[name=mqd_pscode]': {
    			afterrender: function(t){
    				t.gridKey = "mq_pstype";
    				t.mappinggirdKey = "ps_type";
    				t.gridErrorMessage = "请填写模具类型!";
    			}
    		},
    		'multidbfindtrigger[name=mqd_pscode]': {
    			afterrender: function(t){
    				t.gridKey = "mq_pstype";
    				t.mappinggirdKey = "ps_type";
    				t.gridErrorMessage = "请填写模具类型!";
    			}
    		}
		});
	}, 
	getprice: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var price = 0, mouldprice = 0;
		Ext.each(items,function(item,index){
			if(item.data['mqd_pscode']!=null&&item.data['mqd_pscode']!=""){
				price= price + Number(item.data['mqd_price']);
				mouldprice= mouldprice + Number(item.data['mqd_mouldprice']);
			}
		});
		Ext.getCmp('mq_price').setValue(Ext.util.Format.number(price,'0.000000'));
		Ext.getCmp('mq_mouldprice').setValue(Ext.util.Format.number(mouldprice,'0.000000'));
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});