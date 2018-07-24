/**
 * 业务员转预收
 */
Ext.define('erp.view.core.button.SellerPreRec', {
	extend : 'Ext.Button',
	alias : 'widget.erpSellerPreRecButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpSellerPreRecButton,
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
			if(status && status.value != 'POSTED'){
				btn.hide();
			}
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('sellerprerec-win'), form = Ext.getCmp('form');
		if(!win) {
			var f = Ext.getCmp('pr_jsamount'),val = f ? f.value : 0;
			var f2 = Ext.getCmp('pr_havebalance'), val2 = f2 ? f2.value : 0;
			win = Ext.create('Ext.Window', {
				id: 'sellerprerec-win',
				title: '业务员转预收',
				height: 200,
				width: 400,
				items: [{
					fieldLabel: '新业务员',
					labelWidth: 100,
					layout: 'column',
					height: 33,
					xtype: 'fieldcontainer',
					defaults: {
						fieldStyle : "background:#FFFAFA;color:#515151;"
					},
					items: [{
						xtype: 'dbfindtrigger',
						name: 'em_code',
						id: 'em_code',
						columnWidth: 0.4,
						listeners: {
							aftertrigger: function(f, d) {
								f.setValue(d.data.em_code);
								f.ownerCt.down('textfield[name=em_name]').setValue(d.data.em_name);
							}
						}
					},{
						xtype: 'textfield',
						name: 'em_name',
						columnWidth: 0.6,
						readOnly: true,
						fieldStyle: 'background:#f1f1f1;'
					}]
				},{
					xtype:'separnumberfield',
					fieldLabel: '转预收金额',
					name:'thisamount',
	    		    value: val-val2,
	    		    readOnly: false
				}],
				closeAction: 'hide',
				layout : 'column',
				defaults: {
					columnWidth: 1,
					margin: '10 20 0 20'
				},				
				buttonAlign: 'center',
				buttons: [{
					text: $I18N.common.button.erpConfirmButton,
					cls: 'x-btn-gray',
					handler: function(btn) {
						var emcode = btn.ownerCt.ownerCt.down('dbfindtrigger[name=em_code]');
						var thisamount = btn.ownerCt.ownerCt.down('textfield[name=thisamount]');
						if (Ext.isEmpty(emcode.value)){
	 						showError('新业务员必须填写！');
	 						return;
	 					}
	 					if (Ext.isEmpty(thisamount.value) || thisamount.value == 0){
	 						showError('转预收金额必须填写！');
	 						return;
	 					}
	 					if(form.BaseUtil.numberFormat(thisamount.value,2) > form.BaseUtil.numberFormat(val-val2,2)){
	 						showError('本次转预收金额金额['+ form.BaseUtil.numberFormat(thisamount.value, 2) + ']超过预收冲账金额-已冲账金额['+form.BaseUtil.numberFormat(val-val2,2)+']！');
	 						return;
	 					}
						if((emcode.isDirty() && !Ext.isEmpty(emcode.value))||(thisamount.isDirty() && !Ext.isEmpty(thisamount.value))) {
							me.sellerPreRec(Ext.getCmp('pr_id').value, emcode.value, thisamount.value);
						}
					}
				}, {
					text: $I18N.common.button.erpCloseButton,
					cls: 'x-btn-gray',
					handler: function(btn) {
						btn.ownerCt.ownerCt.hide();
					}
				}]
			});
		}
		win.show();
	},
	sellerPreRec: function(id, emcode, thisamount) {
		Ext.Ajax.request({
			url: basePath + 'fa/ars/sellerPreRec.action',
			params: {
				caller: caller,
				id: id,
				emcode: emcode,
				thisamount: thisamount
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					if(rs.success && rs.content){
		   				var msg = '预收账款单号:<br>';
		   				Ext.Array.each(rs.content, function(item){
		   					if(item.errMsg) {
		   						msg += item.errMsg + '<hr>';
		   					} else if(item.id) {
		   						msg += '<a href="javascript:openUrl2(\'jsps/fa/ars/preRec.jsp?whoami=PreRec!Ars!DERE&formCondition=pr_idIS' 
	    							+ item.id + '&gridCondition=prd_pridIS' + item.id + '\',\'预收账款\',\'pr_id\','+item.id+');">' + item.code + '</a><hr>';	
		   					}
		   				});
    					showMessage('提示', msg);
		   			}
				}
			}
		});
	}
});