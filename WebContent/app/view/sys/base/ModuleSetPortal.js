Ext.define('erp.view.sys.base.ModuleSetPortal',{
	extend: 'Ext.form.Panel', 
	alias: 'widget.modulesetportal',
	autoScroll : true,
	labelSeparator : ':',
	buttonAlign : 'center',
	bodyStyle : 'background:#f9f9f9;',
	fieldDefaults : {
		msgTarget: 'none',
		blankText : $I18N.common.form.blankText
	},
	bodyBorder: false,
	border: false,
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	layout:'column',
	//glyph:'66@My Font Family',
	items:[{
		xtype:'label',
		html:'<span style="font-weight:bold;" >基础设置 <span style="color:gray;font-size:10px!important; ">(点击 ? 可查看设置项详细描述)</span></span>',
		columnWidth:1,
		margin:'0 0 0 0'	
	}],
	defaults:{
		xtype:'textfield',
		columnWidth:0.33,
		margin:'5 5 5 5'
	},
	tipTpl: Ext.create('Ext.XTemplate', '<ul class="' + Ext.plainListCls + '"><tpl for="."><li><span class="field-name">{help}</span>!</li></tpl></ul>'),
	initComponent : function(){
		this.loadConfigs(this.condition,this.setConfigs,this);
		this.loadInterceptors(this.condition, this.setInterceptors,this);
		this.callParent(arguments);
	},
	loadConfigs: function(condition, callback,panel) {
		condition=condition==null?"caller="+this.caller:condition;
		Ext.Ajax.request({
			url: basePath + 'ma/setting/getConfigsByCondition.action?condition='+condition,
			method: 'GET',
			callback: function(opt, s, r) {
				if(r && r.status == 200) {
					var res = Ext.JSON.decode(r.responseText);
					callback.call(null, res,panel);
				}
			}
		});
	},
	setConfigs: function(configs,panel) {
		var me = this,items = [];
		Ext.Array.each(configs, function(c, i){
			switch(c.data_type) {
			case 'YN':
				items.push({   		
					xtype: 'checkbox',
					boxLabel: c.title,
					name: c.code,
					checked: c.data == 1,
					columnWidth:panel.itemColumnWidth||.5,
					margin: '0 5 0 0',
					allowBlank: false,
					id:"configs-"+c.id,
					help:c.help,
					afterBoxLabelTextTpl:c.help?'<a href="#" class="help-terms" itemId=configs-'+c.id+'>?</a>':'',
					listeners: {
						click: {
							element: 'boxLabelEl',
							fn: function(e,el) {
								var target = e.getTarget('.help-terms'),
								win,itemId=el.getAttribute("itemId"),item=Ext.getCmp(itemId);
								e.preventDefault();
								item.up('modulesetportal').setHelp(item.help,e);
							}
						}
					}
				});
				break;
			case 'RADIO':
				var s = [];
				Ext.Array.each(c.properties, function(p){
					s.push({
						name: c.code,
						boxLabel: p.display,
						inputValue: p.value,
						checked: p.value == c.data
					});
				});
				items.push({
					xtype: 'radiogroup',
					id:"configs-"+c.id,
					fieldLabel: c.title,
					columnWidth: panel.itemColumnWidth || .5,
					columns: 1,
					vertical: true,
					items: s,
					help:c.help,
					afterLabelTextTpl:c.help?'<a href="#" class="help-terms" itemId=configs-'+c.id+'>?</a>':'',
					listeners: {
						click: {
							element: 'labelEl',
							fn: function(e,el) {
								var target = e.getTarget('.help-terms'),
								win,itemId=el.getAttribute("itemId"),item=Ext.getCmp(itemId);
								e.preventDefault();								
								item.up('modulesetportal').setHelp(item.help,e);
								console.log(item);
							}
						}
					}
				});
				break;
			case 'COLOR':
				items.push({
					xtype: 'colorfield',
					fieldLabel: c.title,
					id:"configs-"+c.id,
					name: c.code,
					value: c.data,
					readOnly: c.editable == 0,
					editable: c.editable == 1,
					labelWidth: 150,
					columnWidth:0.5
				});
				break;
			case 'NUMBER':
				items.push({
					xtype: 'numberfield',
					fieldLabel: c.title,
					id:"configs-"+c.id,
					name: c.code,
					value: c.data,
					readOnly: c.editable == 0,
					labelWidth: 150,
					//maxWidth:400,
					columnWidth:panel.itemColumnWidth || .5,
					help:c.help,
					clearable: true,
					afterLabelTextTpl:c.help?'<a href="#" class="help-terms" itemId=configs-'+c.id+'>?</a>':'',
					listeners:{
						render : function(field) {  
							var p = document.createElement("td");  
							//padding:2cm 4cm 3cm 4cm;
							p.setAttribute('style'," color:gray; width:100px;line-height:22px;");
							var redStar = document.createTextNode('光标离开完成修改');  
							p.appendChild(redStar);  
							field.el.dom.firstChild.firstChild.appendChild(p);  
						},
						click: {
							element: 'labelEl',
							fn: function(e,el) {
								var target = e.getTarget('.help-terms'),
								win,itemId=el.getAttribute("itemId"),item=Ext.getCmp(itemId);
								e.preventDefault();								
								item.up('modulesetportal').setHelp(item.help,e);
								console.log(item);
							}
						}
					}
				});
				break;
			default :
				if(c.multi == 1) {
					var data = c.data ? c.data.split('\n') : [null], s = [];
					Ext.Array.each(data, function(d){
						s.push({
							xtype: (c.dbfind ? 'dbfindtrigger' : 'textfield'),
							name: c.dbfind || c.code,
							value: d,
							readOnly: !c.dbfind && c.editable == 0,
							editable: c.editable == 1,
							clearable: true
						});
					});
					s.push({
						xtype: 'button',
						text: '添加',
						width: 22,
						maxWidth:22,
						cls: 'x-dd-drop-ok-add',
						iconCls: 'x-dd-drop-icon',
						iconAlign: 'right',
						config: c
					});
					items.push({
						xtype: 'fieldset',
						title: c.title,
						id:"configs-"+c.id,
						name: c.code,
						columnWidth:1,
						layout: 'column',
						defaults: {
							columnWidth: .25,
							margin: '4 8 4 8'
						},
						items: s
					});
				} else {
					items.push({
						xtype: (c.dbfind ? 'dbfindtrigger' : 'textfield'),
						fieldLabel: c.title,
						afterLabelTextTpl:c.help?'<a href="#" class="help-terms" itemId=configs-'+c.id+'>?</a>':'',
						id:"configs-"+c.id,
						name: c.dbfind || c.code,
						value: c.data,
						help:c.help,
						readOnly: !c.dbfind && c.editable == 0,
						editable: c.editable == 1,
						clearable: true,
						columnWidth: .4,
						labelWidth: 150,
						dbCaller:c.caller,
						//minWidth:300,
						listeners: {
							click: {
								element: 'labelEl',
								fn: function(e,el) {
									var target = e.getTarget('.help-terms'),
									win,itemId=el.getAttribute("itemId"),item=Ext.getCmp(itemId);
									e.preventDefault();								
									item.up('modulesetportal').setHelp(item.help,e);
			
								}
							}
						}
					});
				}
			break;
			}
			if(c.help) {
				/*items.push({
					xtype: 'fieldset',
					html: c.help,
					columnWidth:1,
					title:'详细描述',
					collapsible: true,
					collapsed: true,
					//cls: 'help-block',
					margin: '4 8 8 8'
				});*/
			} else {
				if(['NUMBER', 'VARCHAR2'].indexOf(c.data_type) > -1) {
					items.push({
						xtype: 'displayfield'
					});
				}
			}
		});
		if(items.length == 0)
			items.push({
				html: '没有参数配置',
				cls: 'x-form-empty'
			});
		panel.add(items);
	},
	setInterceptors: function(Interceptors,panel) {
		var me = this,items = [];
		Ext.Array.each(Interceptors, function(c, i){
			items.push({   		
				xtype: 'checkbox',
				boxLabel: c.title,
				checked: c.data == 1,
				columnWidth:.5,
				margin: '0 5 0 0',
				allowBlank: false,
				id:"interceptors-"+c.id,
				help:c.help,
				afterBoxLabelTextTpl:c.help?'<a href="#" class="help-terms" itemId=interceptors-'+c.id+'>?</a>':'',
				listeners: {
					click: {
						element: 'boxLabelEl',
						fn: function(e,el) {
							var target = e.getTarget('.help-terms'),
							win,itemId=el.getAttribute("itemId"),item=Ext.getCmp(itemId);
							e.preventDefault();
							item.up('modulesetportal').setHelp(item.help,e);
						}
					}
				}
			});

			if(c.help) {
				/*items.push({
					xtype: 'fieldset',
					html: c.help,
					columnWidth:1,
					title:'详细描述',
					collapsible: true,
					collapsed: true,
					//cls: 'help-block',
					margin: '4 8 8 8'
				});*/
			} else {
				if(['NUMBER', 'VARCHAR2'].indexOf(c.data_type) > -1) {
					items.push({
						xtype: 'displayfield'
					});
				}
			}
		});
		if(items.length>0)
		panel.add(items);
	},
	loadInterceptors: function(condition, callback,panel) {
		Ext.Ajax.request({
			url: basePath + 'ma/setting/getInterceptorsByCondition.action?condition=' + condition,
			method: 'GET',
			callback: function(opt, s, r) {
				if(r && r.status == 200) {
					var res = Ext.JSON.decode(r.responseText);
					callback.call( null,res,panel);
				}
			}
		});
	},
	getTip: function(position) {
		var tip = this.tip
		if (!tip) {
			tip = this.tip = Ext.widget('tooltip', {
				title: '详细描述:',
				minWidth: 200,
				autoHide: true,
				anchor: 'top',
				closable: true,
				cls: 'errors-tip'
			});

		}
		tip.showAt([position[0]-19,position[1]+9]);
		return tip;
	},
	setHelp: function(help,e) {
		var me = this,
		tip = me.getTip(e.getXY());
		if (help) {
			tip.setDisabled(false);
			tip.update(me.tipTpl.apply({help:help}));
		} else {
			tip.hide();
		}
	},
	showResult : function(title,format,btn){
		if(!msgCt){
			msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
		}
		var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 1));
		var m = Ext.DomHelper.append(msgCt, createBox(title, s), true);
		m.hide();
		m.slideIn('t').ghost("t", { delay: 1000, remove: true});
	}
});