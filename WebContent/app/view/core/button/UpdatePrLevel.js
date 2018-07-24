/**
 * 更新物料优选等级
 */
Ext.define('erp.view.core.button.UpdatePrLevel', {
	extend : 'Ext.Button',
	alias : 'widget.erpUpdatePrLevelButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpUpdatePrLevelButton,
	style : {
		marginLeft : '10px'
	},
	width : 120,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function(btn) {
			var status = Ext.getCmp('pr_statuscode');
			if(status && status.value == 'ENTERING'){
				btn.hide();
			}
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('prlevel-win');
		if(!win) {
			var f = Ext.getCmp('pr_level'),
				val = f ? f.value : ''; 
			win = Ext.create('Ext.Window', {
				id: 'prlevel-win',
				title: '更新物料等级',
				height: 200,
				width: 400,
				items: [{
					margin: '10 0 0 0',
					xtype: 'dbfindtrigger',
					name:'pr_level',
					fieldLabel: '物料等级', 
					readOnly:false,
					allowBlank: false,
					value: val
				},
				{
					margin: '10 0 0 0',
					xtype: 'textfield',
					fieldLabel: '物料等级备注',
					name:'levelremark',
					value: ''
				}],
				closeAction: 'hide',
				buttonAlign: 'center',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				buttons: [{
					text: $I18N.common.button.erpConfirmButton,
					cls: 'x-btn-blue',
					handler: function(btn) {
						var tx = btn.ownerCt.ownerCt.down('textfield[name=pr_level]');  
						var remark=btn.ownerCt.ownerCt.down('textfield[name=levelremark]').value;
	 					if (remark==null || remark==''){
	 						showError('物料等级备注必须填写');
	 						return;
	 					}
						if((tx.isDirty() && !Ext.isEmpty(tx.value))) {
							me.updateProductLevel(Ext.getCmp('pr_id').value, tx.value,remark);
						}
					}
				}, {
					text: $I18N.common.button.erpCloseButton,
					cls: 'x-btn-blue',
					handler: function(btn) {
						btn.ownerCt.ownerCt.hide();
					}
				}]
			});
		}
		win.show();
	},
	updateProductLevel: function(id, val1,remark) {
		Ext.Ajax.request({
			url: basePath + 'scm/product/updateProductLevel.action',
			params: {
				id: id,
				value: val1 ,
				remark:remark
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					alert('设置成功!');
					window.location.reload();
				}
			}
		});
	},
	getComboData:function(caller,field){
		var combodata=null;
		Ext.Ajax.request({
			url : basePath +'common/getComboDataByCallerAndField.action',
			params: {
				caller:caller,
				field:field
			},
			async: false,
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);

					return;
				}
				if(res.success){
					combodata=res.data;
				}

			} 

		});
		if(combodata.length<1){
			this.add10EmptyData(combodata,caller, field);
		}
		return combodata;
	}
});