Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.FittingBom', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.FittingBom','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.button.Scan',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					var bool = true;
			        //数量不能为空或0
			        Ext.each(items,function(item) {
			            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
			                if (item.data['fbd_qty'] == null || item.data['fbd_qty'] == '' || item.data['fbd_qty'] == '0' || item.data['fbd_qty'] == 0) {
			                    bool = false;
			                    showError('明细表第' + item.data['fbd_detno'] + '行的数量为空');
			                    return;
			                }
			            }
			        });
			        if (bool)
						this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('fb_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var bool = true;
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
			        //数量不能为空或0
			        Ext.each(items,function(item) {
			            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
			                if (item.data['fbd_qty'] == null || item.data['fbd_qty'] == '' || item.data['fbd_qty'] == '0' || item.data['fbd_qty'] == 0) {
			                    bool = false;
			                    showError('明细表第' + item.data['fbd_detno'] + '行的数量为空');
			                    return;
			                }
			            }
			        });
			        if (bool)
						this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addFittingBom', '新增订单配件清单', 'jsps/scm/sale/fittingBom.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('fb_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('fb_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('fb_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('fb_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('fb_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('fb_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('fb_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('fb_id').value);
				}
			},
			'textfield[name=fb_code]':{
				change: function(field){
    				if(field.value != null && field.value != ''){
    					var grid = Ext.getCmp('grid');
    					var code = Ext.getCmp('fb_code').value;
    					var insert = true;//是否需要加入到grid
    					var num = 0;//grid的有效数据有多少行
    					Ext.each(grid.getStore().data.items, function(){
    						if(this.data['fbd_code'] != null && this.data['fbd_code'] != ''){
    							num++;
    							if(this.data['fbd_code'] == code){
        							insert = false;
        						}
    						}
    					});
    					if(num == grid.getStore().data.items.length){
    						me.GridUtil.add10EmptyItems(grid);
    					}
    					if(insert){
    						grid.getStore().data.items[num].set('fbd_code', code);
    					}
    				}
    			}
    		},
    		'dbfindtrigger[name=fb_prodcode]': {
    			afterrender: function(f) {
    				f.autoDbfind = false;
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