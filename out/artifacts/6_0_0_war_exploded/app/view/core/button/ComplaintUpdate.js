/**
 * 投诉信息修改按钮
 */	
Ext.define('erp.view.core.button.ComplaintUpdate',{ 
	extend : 'Ext.Button',
	alias : 'widget.erpComplaintUpdateButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpComplaintUpdateButton,
	style : {
		marginLeft : '10px'
	},
	width : 140,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function(btn) {
			var status = Ext.getCmp('cr_statuscode');
			if(status && status.value == 'ENTERING'){
				btn.hide();
			}
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('Complaint-win');
		if(!win) {
			var result = Ext.getCmp('cr_result'), man = Ext.getCmp('cr_dutyman'), dep = Ext.getCmp('cr_dutydepartment'),cont=Ext.getCmp('cr_content'),
			improve = Ext.getCmp('cr_improve'),val0 = improve ? improve.value : '',
			val1 = result ? result.value : '', val2 =  man ? man.value : '', val3 =   dep ? dep.value : '', val4 =   cont ? cont.value : '';
			win = Ext.create('Ext.Window', {
				id: 'Complaint-win',
				title: '更新投诉单 ' + Ext.getCmp('cr_code').value + ' 的投诉信息',
				height: 300,
				width: 450,
				items: [
					{
					margin: '10 0 0 0',
					xtype: 'textareatrigger',
					fieldLabel: '改善措施',
					name:'imp',
					value: val0
				},													
				{
					margin: '10 0 0 0',
					//xtype: 'textfield',
					xtype: 'textareatrigger',
					fieldLabel: '投诉处理结果',
					name:'res',
					value: val1
				},				
				{
					margin: '3 0 0 0',
					xtype: 'dbfindtrigger',
					fieldLabel: '责任人',
					name:'cr_dutyman',
					value: val2
				},{
					margin: '3 0 0 0',
					xtype: 'dbfindtrigger',
					fieldLabel: '责任部门',
					name:'cr_dutydepartment',
					value: val3
				},{
					margin: '10 0 0 0',
					xtype: 'textareatrigger',
					fieldLabel: '投诉内容',
					name:'cr_content',
					value: val4
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
							e = form.down('textfield[name=imp]'),
							a = form.down('textfield[name=res]'),
							b = form.down('dbfindtrigger[name=cr_dutyman]'),
							c = form.down('dbfindtrigger[name=cr_dutydepartment]'),
						    d =form.down('textareatrigger[name=cr_content]');
						if((a.isDirty() && !Ext.isEmpty(a.value)) || (
								b.isDirty() && !Ext.isEmpty(b.value)) || (
										c.isDirty() && !Ext.isEmpty(c.value))|| (
												d.isDirty() && !Ext.isEmpty(d.value))||(
													e.isDirty() && !Ext.isEmpty(e.value))) {
							me.updateComplaint(Ext.getCmp('cr_id').value, a.value, b.value, c.value,d.value,e.value);
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
	updateComplaint: function(id, val1, val2, val3,val4,val0) {
		Ext.Ajax.request({
			url: basePath + 'scm/qc/updateComplaint.action',
			params: {
				id: id,
				val1: val1,
				val2: val2,
				val3: val3,
				val4: val4,
				val0: val0
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