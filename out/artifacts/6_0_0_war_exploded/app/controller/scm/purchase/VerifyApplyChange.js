Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.VerifyApplyChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.VerifyApplyChange','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.form.FileField', 'core.form.MultiField','core.trigger.MultiDbfindTrigger',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumnNV'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick,
				reconfigure: function(grid, store, columns){
					var detail = getUrlParam('detail');
					gridCondition = getUrlParam('gridCondition');
					if(detail&&!gridCondition){
						me.GridUtil.autoDbfind(grid, 'vcd_vacode', detail);
					}
				}
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item) {
			            if (!Ext.isEmpty(item.data['vcd_vacode'])) {
			                if (item.data['vcd_newqty'] > item.data['vcd_oldqty']) {
			                    bool = false;
			                    showError('明细表第' + item.data['vcd_detno'] + '行的新数量不能大于原数量');
			                    return;
			                }
			            }
			        });
			        if (bool){
			        	if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
							me.BaseUtil.getRandomNumber();//自动添加编号
						}
						this.FormUtil.beforeSave(this);
			        }
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('vc_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item) {
			            if (!Ext.isEmpty(item.data['vcd_vacode'])) {
			                if (item.data['vcd_newqty'] > item.data['vcd_oldqty']) {
			                    bool = false;
			                    showError('明细表第' + item.data['vcd_detno'] + '行的新数量不能大于原数量');
			                    return;
			                }
			            }
			        });
			        if (bool){
			        	this.FormUtil.onUpdate(this);
			        }
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addVerifyApplyChange', '新增收料变更单', 'jsps/scm/purchase/verifyApplyChange.jsp?whoami=' + caller);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vc_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item) {
			            if (!Ext.isEmpty(item.data['vcd_vacode'])) {
			                if (item.data['vcd_newqty'] > item.data['vcd_oldqty']) {
			                    bool = false;
			                    showError('明细表第' + item.data['vcd_detno'] + '行的新数量不能大于原数量');
			                    return;
			                }
			            }
			        });
			        if (bool){
			        	me.FormUtil.onSubmit(Ext.getCmp('vc_id').value);
			        }
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('vc_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vc_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('vc_id').value);
				}
			},
			'dbfindtrigger[name=vcd_vadetno]': {
				focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['vcd_vacode'];
    				var grid = Ext.getCmp('grid');
    				if(code == null || code == ''){
    					showError("请先选择收料单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "va_code='" + code + "'";
    				}
    				var cond ="(va_code='"+code+"' ";
    				var arr = new Array();
    				Ext.each(grid.store.data.items, function(item){
    					if(item.data['vcd_sacode'] != null && item.data['vcd_sacode'] != ''
    						&& item.data['vcd_sddetno'] != null && item.data['vcd_sddetno'] !=''){
    						if(item.data['vcd_sacode'] ==code){
    							arr.push("sd_detno<>"+item.data['vcd_sddetno']);
    						}
    					}
    				});
    				if(arr.length > 0){
    					cond += ' AND ' + arr.join(' and ');
    				}
    				t.dbBaseCondition = cond + ") ";
    			}
    		},
    		'multidbfindtrigger[name=vcd_sddetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['vcd_sacode'];
    				var grid = Ext.getCmp('grid');
    				if(code == null || code == ''){
    					showError("请先选择订单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "sa_code='" + code + "'";
    				}
    				var cond ="(sa_code='"+code+"' ";
    				var arr = new Array();
    				Ext.each(grid.store.data.items, function(item){
    					if(item.data['vcd_vacode'] != null && item.data['vcd_vacode'] != ''
    						&& item.data['vcd_vadetno'] != null && item.data['vcd_vadetno'] !=''){
    						if(item.data['vcd_vacode'] ==code){
    							arr.push("vad_detno<>"+item.data['vcd_vadetno']);
    						}
    					}
    				});
    				if(arr.length > 0){
    					cond += ' AND ' + arr.join(' and ');
    				}
    				t.dbBaseCondition = cond + ") ";
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