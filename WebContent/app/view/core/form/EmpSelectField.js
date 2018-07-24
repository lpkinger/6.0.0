Ext.define('erp.view.core.form.EmpSelectField', {
	extend: 'Ext.form.FieldContainer',
	alias: 'widget.EmpSelectfield',
	requires:['erp.view.core.form.OrgTreePanel1','erp.view.core.form.ItemSelector1','erp.view.hr.attendance.WdTreePanel'],
	layout: 'column',
	hideLabel:true,
	autoScroll:false,
	style:"border-right:1px solid #D5D5D5;border-top:1px solid #D5D5D5;border-bottom:1px solid #D5D5D5;",
	initComponent : function(){
		this.callParent(arguments);
		this.cls='';
		var me = this;
		me.addEvents({afterclick: true});
		me.insert(0, {
			xtype: 'htmleditor',
			enableColors: false,
			enableAlignments: false,
			columnWidth:1,
			enableFont: false,
			enableFontSize: false,
			enableFormat: false,
			enableLinks: false,
			enableLists: false,
			enableSourceEdit: false,
			labelSeparator :'',
			fieldLabel:'<div style="margin-top:63px">'+this.fieldLabel+'</div>',
			name:me.name,
			editable: false,
			allowBlank:false,
			readOnly:true,
			autoHeight:true,
			height:120,
			labelWidth:105,
			labelStyle:'background-color:#D5D5D5;height:153px;margin-right:2px !important;',
			value:me.value,
			listeners:{
				afterrender:function(editor){
					editor.getToolbar().hide();
				}
			}
			
		});
		me.insert(1,{ xtype: 'label',
			readOnly:true,
			labelWidth:400,
			labelSeparator:'',
			columnWidth:0.6,
			padding:'0 0 0 105',
			height:28,
			fieldStyle : 'background:#f0f0f0;border-bottom:none;vertical-align:middle;border-top:none;border-right:none;border-left:none;',
			html: '『<a href="#" class="terms">选择</a>』(选择需要相应的人员、岗位或组织)',
			columnWidth:1,
			listeners: {
				render : function() {//渲染后添加click事件
			          Ext.fly(this.el).on('click',
			            function(e, t) {
							var target = e.getTarget('.terms'),
							win;
							if (target) {
								var data=new Array(),value,display;
								me.secondvalue=me.secondvalue!=undefined?me.secondvalue:Ext.getCmp(me.logic).value;
								if(me.secondvalue){
									value=me.secondvalue.split(";");							
									var displayfield=me.items.items[0];
									if(displayfield.value==''){
										displayfield.checkChange();
										displayfield.initValue();
										me.value=displayfield.value;
									}
									var displayvalue=me.value;
									displayvalue=displayvalue && displayvalue!=''?displayvalue:me.items.items[0].value;
									display=displayvalue.substring(displayvalue.indexOf(">")+1,me.value.lastIndexOf("<")).split(";");
								}
								Ext.Array.each(value,function(item,index){
									data.push({
										text:display[index],
										value:item,
										value1:display[index]
									});
								});
								win = Ext.widget('window', {
									title: '<div align="center" class="WindowTitle">选择对象</div>',
									modal: true,
									width:'80%',
									height:'90%',
									layout:'border',
									requires:['erp.view.core.form.ItemSelector1'],
									items:[{
										region:'center',
										layout:'border',
										items:[{
											region:'north',
											xtype:'form',
											bodyPadding: 10,
											layout:'column',
											bodyStyle:'background:#fafafa;',
											items:[{
												xtype:'textfield',
												margin:'0 0 0 20',
												fieldLabel:'快速搜索',
												labelStyle:'font-weight:bold;',
												columnWidth:0.8
											},{
												xtype:'button',
												id:'search',
												text:'搜索',
												cls:'button1 pill',
												style:'margin-left:5px;',
												width:60,
												handler:function(btn){
													btn.setDisabled(true);
													var likestring=btn.ownerCt.items.items[0].value;
													if(!likestring) {
														showMessage('提示','请输入需要搜索的信息!',1000);
														btn.setDisabled(false);
														return;
													}
													Ext.Ajax.request({//查询数据
														url : basePath + '/hr/attendance/search.action',
														params:{
															likestring:likestring
														},
														callback : function(options,success,response){
															var res = new Ext.decode(response.responseText);													
															if(res.data){
																Ext.getCmp('itemselector-field').fromField.store.loadData(res.data);
																btn.setDisabled(false);
															} else if(res.exceptionInfo){
																showError(res.exceptionInfo);
															}
														}
													});
												}
											}]
										},{
											region:'center',
											xtype: 'itemselector1',
											data:data,
											anchor: '100%',	
											id: 'itemselector-field',
											displayField: 'text',
											valueField: 'value',
											allowBlank: false,
											msgTarget: 'side',
											listeners:{
												afterrender:function(field){
													field.toField.store.loadData(data);
												}
											}
										}],
										buttonAlign:'center',
										buttons:['->',{
											cls:'button1 pill',
											style:'margin-left:5px;',
											text:'确认',
											scope:this,
											handler:function(btn){
												var itemselector=Ext.getCmp('itemselector-field');
												var value=itemselector.getRawValue();
												if(value.length<1){
													showMessage('提示','选择需要设置对象',1000);
												}else {
													var toField=itemselector.toField,displayValue;
													if (toField) {
														displayValue = Ext.Array.map(toField.boundList.getStore().getRange(), function(model) {
															return model.get(itemselector.displayField);
														});
													}	
													me.setFieldValue(displayValue,value);
													btn.ownerCt.ownerCt.ownerCt.close();
												}
											}
										},{
											cls:'button1 pill',
											style:'margin-left:5px;',
											text:'关闭',
											handler:function(btn){
												btn.ownerCt.ownerCt.ownerCt.close();
											}
										},'->']
									},{
										region:'west',
										width:'40%',
										margins: '0 0 0 0',
										border:'0 0 0 0',
										layout: 'accordion',
										items: [{					    
											title: '公司组织架构',
											xtype:'erpOrgTreePanel1',
											bodyStyle:'background:#fafafa;'
										},{
											title: '默认班次',
											itemId:'emwdcode',
											xtype:'erpWdTreePanel',
											bodyStyle:'background:#fafafa;'
										}]

									}]
								});
								win.show();
							  	me.fireEvent('afterclick');
								e.preventDefault();
						}	
			    });
			}
		 }	
		});
	  this.firstField=this.items.items[0];
	},
	isValid: function(){
		return this.firstField.isValid();
	},
	setValue: function(value){
		this.firstField.setValue(value);
	},
	getValue: function(){
		return this.value;
	},
	setFieldValue:function(displayValue,rawValue){
		var me=this;
		me.items.items[0].setValue('<font size="2.5">'+displayValue.join(";")+"</font>");
		Ext.getCmp(me.secondname).setValue(rawValue.join(";"));
		me.value='<font size="2.5">'+displayValue.join(";")+"</font>";
		me.secondvalue=rawValue.join(";");
	}
});