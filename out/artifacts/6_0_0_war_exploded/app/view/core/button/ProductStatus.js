/**
 * 更新物料承认状态
 */
Ext.define('erp.view.core.button.ProductStatus', {
	extend : 'Ext.Button',
	alias : 'widget.erpProductStatusButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpProductStatusButton,
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
		var me = this, win = Ext.getCmp('prmaterial-win');
		if(!win) {
			var f = Ext.getCmp('pr_material'),val = f ? f.value : '';
			var f2 = Ext.getCmp('pr_crman'), val2 = f2 ? f2.value : '';
			var f3 = Ext.getCmp('pr_attach'), val3 = f3 ? f3.value : '';
			var f4 = Ext.getCmp('pr_admitstatus'),val4 = f4 ? f4.value : '';
			win = Ext.create('Ext.Window', {
				id: 'prmaterial-win',
				title: '更改承认状态',
				height: 300,
				width: 400,
				items: [{
					xtype: 'combo',
					name:'pr_material',
					fieldLabel: '承认状态', 
					store: {
						fields: ['dlc_display', 'dlc_value'],
						data :[],
					},
					displayField: 'dlc_display',
					valueField: 'dlc_value', 
					onTriggerClick:function(trigger){ 
						var combodata=me.getComboData('Product','pr_material');
						//这里写方法查找combo的数据
						this.getStore().loadData(combodata);
						this.expand(); 
					},
					editable: false, 
					value:val
				},{
					xtype: 'dbfindtrigger',
					fieldLabel: '承认人',
					name:'em_code',
					value: val2,
					listeners:{
						aftertrigger:function(t, d){
							t.ownerCt.down('textfield[name=em_code]').setValue(d.get('em_name')); 
						}
					}
				},{
					xtype: 'datefield',
					fieldLabel: '承认日期',
					name:'pr_sqrq',
					value: new Date(),
					name:'pr_sqrq',
		       	    format:'Y-m-d',
				},
				{
					xtype: 'textfield',
					id:'remark',
					fieldLabel: '<font style="color:#F00">承认备注</font>',
					focusOnToFront:true,
					name:'crremark',
					value: val4 
				},{
					xtype: 'mfilefield',
					fieldLabel: '附件',
					id:'attach',
					name:'attach',
					value: val3
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
					cls: 'x-btn-blue',
					handler: function(btn) {
						var tx = btn.ownerCt.ownerCt.down('combo[name=pr_material]');  
						var emname = btn.ownerCt.ownerCt.down('dbfindtrigger[name=em_code]');
						var remark=btn.ownerCt.ownerCt.down('textfield[name=crremark]'); 
						var date=btn.ownerCt.ownerCt.down('textfield[name=pr_sqrq]');
						var mfile = btn.ownerCt.ownerCt.down('hidden[name=attach]');
	 					if (remark.value==null || remark.value==''){
	 						showError('承认备注必须填写');
	 						Ext.getCmp('remark').focus(false, 100); 
	 						return;
	 					}
//						if(!Ext.isEmpty(tx.value) || !Ext.isEmpty(emname.value)) {
							me.updateProductStatus(Ext.getCmp('pr_id').value, tx.value,emname.value,remark.value,Ext.Date.toString(date.value),mfile.value);
//						}else{
//							showError("承认状态或承认人有为空的,无法更新!!!");
//						}
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
	updateProductStatus: function(id, val1,val2,remark, date,mfile) {
		Ext.Ajax.request({
			url: basePath + 'scm/product/updateProductStatus.action',
			params: {
				id: id,
				value: val1,
				crman: val2,
				remark:remark,
				date:date,
				mfile:mfile
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