Ext.QuickTips.init();
Ext.define('erp.controller.ma.PostStyle', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['ma.PostStyle', 'core.form.Panel', 'core.grid.Panel2',
			'core.toolbar.Toolbar','core.button.Save', 'core.button.Add',
			 'core.button.Close', 'core.button.Delete',
			'core.button.Update', 'core.button.DeleteDetail', 'core.trigger.MultiDbfindTrigger',
			'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger',
			'core.form.MultiField','erp.view.core.button.AddDetail',
			'erp.view.core.button.DeleteDetail', 'erp.view.core.button.Copy',
			'erp.view.core.button.Paste', 'erp.view.core.button.Up',
			'erp.view.core.button.Down', 'erp.view.core.button.UpExcel',
			'common.datalist.Toolbar'],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2' : {
				cellclick : function(view, td, colIdx, record, tr, rowIdx, e) {	
					var field = view.ownerCt.columns[colIdx].dataIndex;
					if (field == 'pss_othps') {
						this.onCellItemClick(record);
					};
				}
			},
			'erpSaveButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
					this.FormUtil.beforeSave(this);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					this.FormUtil.beforeClose(this);
				}
			},
			'erpAddButton' : {
				click : function() {
					me.FormUtil
							.onAdd('addPostStyle', '新增同步公式',
									'jsps/ma/poststyle.jsp?whoami=PostStyle');
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					this.FormUtil.onUpdate(this);
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete((Ext.getCmp('ps_id').value));
				}
			},
		});
	},

	onCellItemClick : function(record) {// grid行选择
		var me=this;
		var othps = record.data['pss_othps'];
		if( othps.trim()!="" && othps.indexOf("-")>0){
			Ext.create('Ext.Window', {
				width: 735,
				height: 450,
				autoShow: true,
				layout: 'anchor',
				items: [{
					tag : 'iframe',
					frame : true,
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe id="iframe_'+othps+'" src="' + basePath + 'jsps/common/gridpage.jsp?whoami=PostStyleDetail&gridCondition=psd_fromto=\''+othps+'\'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
				}]
			});
		}else if( othps.trim()!="" && othps.indexOf("-")<0){
			Ext.Ajax.request({
				url : basePath + 'common/getFieldData.action',
				params : {
					field:"ps_id",
					caller:"poststyle",
					condition:"ps_caller='"+othps+"'"
				},
				method : 'post',
				callback : function(options, success, response) {					
					var localJson = new Ext.decode(response.responseText);
					if (localJson.exceptionInfo) {
						showError(localJson.exceptionInfo);
						return;
					}
					if (localJson.success) {
						if(localJson.data){
					var id=localJson.data;
					var url = "jsps/ma/poststyle.jsp?formCondition=ps_id=" + id + "&gridCondition=pss_psid=" + id;					
					me.FormUtil.onAdd('PostStyle' + id, '同步公式' + id, url);					
						}
					}
				}
			});						
		}
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;

	}
});