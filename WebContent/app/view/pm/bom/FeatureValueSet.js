Ext.define('erp.view.pm.bom.FeatureValueSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'FeatureProductViewport', 
				layout: 'anchor', 
				items: [{
					anchor: '100% 38%', 
					xtype:'panel',
					frame : true,
					autoScroll : true,
					fieldDefaults : {
					       margin : '2 2 2 2',
					       fieldStyle : "background:#FFFAFA;color:#515151;",
					       focusCls: 'x-form-field-cir',//fieldCls
					       labelAlign : "right",
					       msgTarget: 'side',
					       blankText : $I18N.common.form.blankText
					},
					layout: 'column', 
					items:[{
						xtype:'dbfindtrigger',
						columnWidth: 0.3,
						cls: "form-field-allowBlank",
						fieldLabel:'虚拟特征料号',
						name:'pr_code',
						id:'pr_code'
					},{
						xtype:'textfield',
						columnWidth: 0.3,
						cls: "form-field-allowBlank",
						fieldLabel:'物料名称',
						readOnly: true,
						name:'pr_name',
						id:'pr_name'
					},{
						xtype:'textfield',
						columnWidth: 0.2,
						cls: "form-field-allowBlank",
						fieldLabel:'ID',
						hidden:true,
						readOnly:true,
						id:'id'
					},{
						xtype:'textfield',
						columnWidth: 1,
						height:'1px',
						fieldStyle: 'background:#FFFAFA;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;border-bottom:none;border-left:none; ',
						readOnly:true
					},{
						xtype:'dbfindtrigger',
						columnWidth: 0.5,
						cls: "form-field-allowBlank",
						fieldLabel:'参考特征件',
						name:'othercode',
						id:'othercode'
					},{
						xtype:'button',
						columnWidth: 0.1,
						text:'设为参考',
						name:'refer',
						id:'refer1'
					},{
						xtype:'textfield',
						columnWidth: 0.1,
						cls: "form-field-allowBlank",
//						fieldLabel:'ID',
						hidden:true,
						readOnly:true,
						name:'pr_specdescription',
						id:'pr_specdescription'
					},{
						xtype:'textfield',
						columnWidth: 1,
						height:'1px',
						fieldStyle: 'background:#FFFAFA;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;border-bottom:none;border-left:none; ',
						readOnly:true
					},{
						xtype:'multifield',
						columnWidth: 0.5,
						split:true,
						cls: "form-field-allowBlank",
						fieldLabel:'参考销售订单',
						name:'sacode',
						id:'sacode',
					    secondname: 'sadetno',//另外的一个字段
   				   },{
						xtype:'button',
						columnWidth: 0.1,
						text:'设为参考',
						name:'refer',
						id:'refer2'
					},{
						xtype:'textfield',
						columnWidth: 0.1,
						cls: "form-field-allowBlank",
//						fieldLabel:'ID',
						hidden:true,
						readOnly:true,
						name:'sd_specdescription',
						id:'sd_specdescription'
					},{
						xtype:'textfield',
						columnWidth: 1,
						height:'1px',
						fieldStyle: 'background:#FFFAFA;padding:2px 2px;vertical-align:middle;border-top:none;border-right:none;border-bottom:none;border-left:none; ',
						readOnly:true
					},{
						xtype:'multifield',
						columnWidth: 0.5,
						cls: "form-field-allowBlank",
						fieldLabel:'参考销售预测',
						name:'sfcode',
						id:'sfcode',
						secondname: 'sfdetno',//另外的一个字段
					},{
						xtype:'button',
						columnWidth: 0.1,
						text:'设为参考',
						name:'refer',
						id:'refer3'
					},{
						xtype:'textfield',
						columnWidth: 0.1,
						cls: "form-field-allowBlank",
//						fieldLabel:'ID',
						hidden:true,
						readOnly:true,
						name:'sfd_specdescription',
						id:'sfd_specdescription'
					}],
					tbar:['->',{
						text:'确定',
						name:'ok',
						id:'ok'
					},{
						text:'关闭',
						name:'cancel',
						id:'cancel'
					}],
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 62%', 
//					title:'特征值明细',
					caller:'FeatureValueSet',
					bbar:[{
						xtype:'textfield',
						readOnly: true,
						fieldLabel:'特征件料号',
						id: 'RealCode',
						name : 'RealCode'
					},'-',{
						text:'保存',
						id:'save'
					},'-',{
						text:'生成料号',
						id:'getrealcode'
					},'->',{
						text:'查看BOM',
						id:'find',
						disabled: true
					}]
				}]
			}] 
		}); 
		me.callParent(arguments); 
	}
});