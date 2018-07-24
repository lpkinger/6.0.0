Ext.QuickTips.init();
Ext.define('erp.controller.co.cost.ProdIOCateSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','co.cost.ProdIOCateSet','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update',
      			'core.button.ResSubmit','core.button.Scan','core.button.Banned','core.button.ResBanned',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.StatusField'
      	],
    init:function(){
    	var me = this;
    	this.control({
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
    				me.FormUtil.onDelete(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
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
    				me.FormUtil.onAdd('addProdIOCateSet', '新增其它出入库科目', 'jsps/co/cost/prodIOCateSet.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pc_statuscode');
					if(status && status.value == 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onBanned(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			afterrender: function(btn){
					var status = Ext.getCmp('pc_statuscode');
					if(status && status.value != 'DISABLE'){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.onResBanned(Ext.getCmp('pc_id').value);
    			}
    		},
    		'textfield[name=pc_class]': {
    			afterrender: function(m) {
    				var f = Ext.getCmp('pc_type');
					if(!Ext.isEmpty(m.value)) {
						switch (m.value) {
							case '其它入库单':
								me.getComboData(f.store, 'ProdInOut!OtherIn');
								break;
							case '其它出库单':
								me.getComboData(f.store, 'ProdInOut!OtherOut');
								break;
							case '用品领用单':
								me.getComboData(f.store, 'ProdInOut!GoodsPicking');
								break;
							case '用品退仓单':
								me.getComboData(f.store, 'ProdInOut!GoodsShutout');
								break;
							case '用品验收单':
								me.getComboData(f.store, 'ProdInOut!GoodsIn');
								break;
							case '用品验退单':
								me.getComboData(f.store, 'ProdInOut!GoodsOut');
								break;
						}
					}
    			},
    			change: function(m){
    				var f = Ext.getCmp('pc_type');
					if(!Ext.isEmpty(m.value)) {
						switch (m.value) {
							case '其它入库单':
								me.getComboData(f.store, 'ProdInOut!OtherIn');
								break;
							case '其它出库单':
								me.getComboData(f.store, 'ProdInOut!OtherOut');
								break;
							case '用品领用单':
								me.getComboData(f.store, 'ProdInOut!GoodsPicking');
								break;
							case '用品退仓单':
								me.getComboData(f.store, 'ProdInOut!GoodsShutout');
								break;
							case '用品验收单':
								me.getComboData(f.store, 'ProdInOut!GoodsIn');
								break;
							case '用品验退单':
								me.getComboData(f.store, 'ProdInOut!GoodsOut');
								break;
						}
					}
				}
    		}
    	});
    }, 
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getComboData: function(store, cal) {
		if(this._combodata && this._combodata[cal]) {
			store.loadData(this._combodata[cal]);
			return;
		}
		var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		async: false,
	   		params: {
	   			caller: 'DataListCombo',
	   			fields: 'dlc_value,dlc_display',
	   			condition: 'dlc_caller=\'' + cal + '\' AND dlc_fieldname=\'pi_type\''
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
    						display: data[i].DLC_VALUE,
    						value: data[i].DLC_DISPLAY
    					});
    				}
    				store.loadData(arr);
    				if(me._combodata == null) {
    					me._combodata = {};
    				}
    				me._combodata[cal] = arr;
	   			}
	   		}
		});
	}
});