/**
 * 更新用料表仓库
 */
Ext.define('erp.view.core.button.UpdateMaterialWH',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateMaterialWHButton',
		param: [],
		id: 'erpUpdateMaterialWHButton',
		text: $I18N.common.button.erpUpdateMaterialWHButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 130,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler: function() {
			var me = this, win = Ext.getCmp('Complaint-win');
			if(!win) {
				win = Ext.create('Ext.Window', {
					id: 'Complaint-win',
					title: '更新用料表仓库',
					height: 200,
					width: 400,
					items: [{
						margin: '10 0 0 0',
						xtype: 'dbfindtrigger',
						fieldLabel: '仓库编号',
						name:'wh_code',
						listeners:{
							aftertrigger:function(t, d){
								t.ownerCt.down('textfield[name=wh_code]').setValue(d.get('wh_code'));
								t.ownerCt.down('textfield[name=wh_description]').setValue(d.get('wh_description'));
							}
						}
					},{
						margin: '3 0 0 0',
						xtype: 'textfield',
						fieldLabel: '仓库名称',
						readOnly:true,
						name:'wh_description'
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
							var form = btn.ownerCt.ownerCt,
								whcode = form.down('dbfindtrigger[name=wh_code]');
							if((whcode.isDirty() && !Ext.isEmpty(whcode.value))) {
								me.updateMaterialWH(Ext.getCmp('ma_id').value, whcode.value);
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
		updateMaterialWH: function(id, whcode) {
			Ext.Ajax.request({
				url: basePath + 'pm/make/updateMaterialWH.action',
				params: {
					id: id,
					whcode: whcode,
					caller: caller
				},
				callback: function(opt, s, r) {
					var rs = Ext.decode(r.responseText);
					if(rs.exceptionInfo) {
						showError(rs.exceptionInfo);
					} else {
						Ext.Msg.alert("提示","更新成功！");
						window.location.reload();
					}
				}
			});
		}
	});