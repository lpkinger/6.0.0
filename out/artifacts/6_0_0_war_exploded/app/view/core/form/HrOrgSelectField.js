Ext.define('erp.view.core.form.HrOrgSelectField', {
	extend: 'Ext.form.FieldContainer',
	alias: 'widget.HrOrgSelectfield',
	requires:['erp.view.oa.doc.OrgTreePanel','erp.view.oa.doc.ItemSelector'],
	layout: 'column',
	isSpecial:false,//贝腾代理商使用
	hideLabel:true,
	autoScroll:false,
	style:"border-right:1px solid #D5D5D5;border-top:1px solid #D5D5D5;border-bottom:1px solid #D5D5D5;",
	initComponent : function(){
		this.callParent(arguments);
		this.cls='';		
		var me = this,labelStyle='line-height:'+((me.height?me.height:120)+25)+'px;background-color:#D5D5D5;height:153px;margin-right:2px !important;';
		if(!me.allowBlank) labelStyle=labelStyle.concat('color:red !important;');
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
			allowBlank:this.allowBlank,
			labelSeparator :'',
			fieldLabel:this.fieldLabel,
			name:me.name,
			editable: false,
			allowBlank:me.allowBlank,
			readOnly:true,
			autoHeight:true,
			height:this.height?(this.height-28):120,
			labelWidth:105,
			labelStyle:labelStyle,
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
			width:400,
			fieldStyle : 'background:#f0f0f0;border-bottom:none;vertical-align:middle;border-top:none;border-right:none;border-left:none;',
			html: '『<a href="#" class="terms">选择</a>』(选择需要相应的人员、岗位或组织)',
			//style:"border-bottom:2px solid #D5D5D5;border-right:1px solid #D5D5D5",
			columnWidth:1,
			listeners: {
				/*click: {
					//element: 'labelEl',
					fn: function(e) {}
				},*/
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
									var displayvalue=me.value.length>=displayfield.value.length?me.value:displayfield.value;
									//displayvalue=!displayvalue && displayvalue!=''?displayvalue:me.items.items[0].value;
									//添加一个手机端输入的判断，如果是手机端输入就检验，并且加上格式
									if(me.value.lastIndexOf("<")=="-1"){
										displayvalue = '<font size="2.5">'+displayvalue+'</font>';
										me.value='<font size="2.5">'+me.value+'</font>';
									}
									display=displayvalue.substring(displayvalue.indexOf(">")+1,me.value.lastIndexOf("<")).split(";");
								}
								Ext.Array.each(value,function(item,index){
									data.push({
										text:display[index],
										value:item
									});
								});
								win = Ext.widget('window', {
									title: '<div align="center" class="WindowTitle">选择对象</div>',
									modal: true,
									width:'80%',
									height:'90%',
									layout:'border',
									requires:['erp.view.oa.doc.ItemSelector'],
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
														url : basePath +(me.isSpecial?'common/ProcessQueryAgentPersons.action':'common/ProcessQueryPersons.action'),
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
											xtype: 'itemselector',
											data:data,
											anchor: '100%',	
											id: 'itemselector-field',
											displayField: 'text',
											valueField: 'value',
											allowBlank: true,
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
												
												var toField=itemselector.toField,displayValue;
												if (toField) {
													displayValue = Ext.Array.map(toField.boundList.getStore().getRange(), function(model) {
														return model.get(itemselector.displayField);
													});
												}	
												me.setFieldValue(displayValue,value);
												btn.ownerCt.ownerCt.ownerCt.close();												
												
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
											xtype:'erpOrgTreePanel',
											bodyStyle:'background:#fafafa;',
											isSpecial:me.isSpecial
										},{
											title: '个人通讯组',
											itemId:'personal'

										},{
											title: '最近联系人',
											itemId:'recent'
										}]

									}]
								});
								win.show();
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
		if(displayValue.length>0){
			me.items.items[0].setValue('<font size="2.5">'+displayValue.join(";")+"</font>");
		}else{
			me.items.items[0].setValue(null);
		}
		
		Ext.getCmp(me.secondname).setValue(rawValue.join(";"));
		me.value='<font size="2.5">'+displayValue.join(";")+"</font>";
		me.secondvalue=rawValue.join(";");
	}
});