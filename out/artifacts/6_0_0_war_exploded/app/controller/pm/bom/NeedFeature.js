Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.NeedFeature', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'core.form.Panel', 'pm.bom.NeedFeature', 'core.grid.Panel2',
			'core.toolbar.Toolbar', 'core.button.Save','core.button.Delete', 'core.button.Close',
			'core.button.Update', 'core.trigger.DbfindTrigger',
			'core.trigger.TextAreaTrigger' ],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2' : {
				reconfigure : function(grid) {
					Ext.getCmp('toolbar').setDisabled(true);
					if(grid.store.getCount()==0){
						var ftcode = Ext.getCmp('pvd_ftcode');
						if(ftcode && !Ext.isEmpty(ftcode.value)){
							me.loadFeature(ftcode.value);
						}
					}
				},
				itemclick : this.onGridItemClick
			},
			'erpSaveButton' : {
				click : function(btn) {
					this.FormUtil.onUpdate(this);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					this.FormUtil.onUpdate(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pvd_id').value);
    			}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'dbfindtrigger[name=nf_valuecode]' : {
				focus : function(t) {
					t.setHideTrigger(false);
					t.setReadOnly(false);
					var record = Ext.getCmp('grid').selModel.getLastSelected();
					var code = record.data['nf_fecode'];
					if (code == null || code == '') {
						showError("请先选择特征编号!");
						t.setHideTrigger(true);
						t.setReadOnly(true);
					} else {
						t.dbBaseCondition = "fe_code='" + code + "'";
					}
				}
			}
		});
	},
	onGridItemClick : function(selModel, record) {// grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},
	loadFeature : function(num) {
		Ext.Ajax.request({// 拿到grid的columns
			url : basePath + "common/loadNewGridStore.action",
			params : {
				caller : 'FeatureTemplet',
				condition : "fd_code='" + num + "'"
			},
			method : 'post',
			callback : function(options, success, response) {
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}
				var data = res.data;
				var fpd = [];
				if (data != null && data.length > 0) {
					Ext.each(data, function(d, index) {
						var da = {
							nf_ftcode : num,
							nf_detno : d.fd_detno,
							nf_fecode : d.fd_fecode,
							nf_fename : d.fd_fename
						};
						fpd[index] = da;
					});
					Ext.getCmp('grid').store.loadData(fpd);
				} else {
					showError('没有可载入的特征');
					return;
				}
			}
		});
	}
});