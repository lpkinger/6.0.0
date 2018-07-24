Ext.define('erp.view.mobile.Main',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true,
	layout: 'border',
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			items: [{
				xtype: 'tabpanel',
				_mobile: true,
				cls: 'custom',
				region: 'center',
				id: 'content-panel',
				tabPosition: 'bottom',
				items: [{
					title: '首页',
					xtype: 'container',
					autoScroll: true,
					items: [{ 
						xtype: 'toolbar',
						height: 35,
						cls: 'custom-top',
						items: [sobText, '->', '<font class="font-face-display">' + em_name + '(' + em_code + ')</font>', '->', {
							text: '退出',
							id: 'logout',
							cls: 'custom-button'
						}]
					},{
						xtype: 'container',
						region: 'center',
						id: 'card',
						layout: 'card',
						items: [{
							xtype: 'container',
							height: 1000,
							layout: 'hbox',
							items: [{
								xtype: 'container',
								layout: 'auto',
								height: 500,
								autoScroll: true,
								flex: 1,
								margin: '5 5 0 0',
								cls: 'custom',
								items: [{
									xtype: 'toolbar',
									region: 'north',
									margin: '5 0 0 0',
									cls: 'custom-tb',
									height: 35,
									items: ['->','导航','->']
								},{
									xtype: 'dataview',
									id: 'treeroot',
									itemSelector: '.custom-button',
							        overItemCls : 'tree-node-hover',
							        enableDragDrop: true,
							        tpl: Ext.create('Ext.XTemplate',
							            '<tpl for=".">',
							            	'<div class="custom-button"',
								            	'<font>{text}</font>',
								            '</div>',
							            '</tpl>'
							        ),
							        store: new Ext.data.Store({
							        	fields: ['text', 'parentId', {name: 'id', type: 'number'}, 'url', 'leaf']
							        })
								}]
							}, {
								flex: 1,
								margin: '5 0 0 0',
								xtype: 'container',
								cls: 'custom',
								height: 1000,
								defaults: {
									xtype: 'container',
									margin: '5 0 0 0',
									cls: 'custom'
								},
								items: [/*{
									margin: '0 0 0 0',
									layout: 'auto',
									items: [{
										xtype: 'toolbar',
										region: 'north',
										margin: '5 0 0 0',
										cls: 'custom-tb',
										height: 35,
										items: ['->','常用模块','->']
									},{
										xtype: 'dataview',
										id: 'commonuse',
										itemSelector: '.custom-button-link',
								        overItemCls : 'tree-node-hover',
								        enableDragDrop: true,
								        tpl: Ext.create('Ext.XTemplate',
								            '<tpl for=".">',
								            	'<div class="custom-button-link"',
									            	'<font>{cu_description}({cu_count})</font>',
									            '</div>',
								            '</tpl>'
								        ),
								        store: new Ext.data.Store({
								        	fields: [{name: 'cu_count', type: 'number'}, 'cu_description', {name: 'cu_emid', type: 'number'}, {name: 'cu_id', type: 'number'}, 'cu_url', {name: 'cu_snid', type: 'number'}]
								        })
									}]
								}, */{
									margin: '0 0 0 0',
									layout: 'auto',
									items: [{
										xtype: 'toolbar',
										region: 'north',
										margin: '5 0 0 0',
										cls: 'custom-tb',
										height: 35,
										items: ['->','我的可选流程','->']
									},{
										xtype: 'dataview',
										id: 'mychoice',
										itemSelector: '.custom-button-link',
								        overItemCls : 'tree-node-hover',
								        enableDragDrop: true,
								        tpl: Ext.create('Ext.XTemplate',
								            '<tpl for=".">',
								            	'<div class="custom-button-link"',
									            	'<font>{taskname}({status})</font>',
									            '</div>',
								            '</tpl>'
								        ),
								        store: new Ext.data.Store({
								        	fields: [{name: 'id', type: 'number'},'taskname', 'status']
								        })
									}]
								},{
									margin: '0 0 0 0',
									layout: 'auto',
									items: [{
										xtype: 'toolbar',
										region: 'north',
										margin: '5 0 0 0',
										cls: 'custom-tb',
										height: 35,
										items: ['->','我的待审批流程','->']
									},{
										xtype: 'dataview',
										id: 'myflow',
										itemSelector: '.custom-button-link',
								        overItemCls : 'tree-node-hover',
								        enableDragDrop: true,
								        tpl: Ext.create('Ext.XTemplate',
								            '<tpl for=".">',
								            	'<div class="custom-button-link"',
									            	'<font>{taskname}({status})</font>',
									            '</div>',
								            '</tpl>'
								        ),
								        store: new Ext.data.Store({
								        	fields: [{name: 'id', type: 'number'},'taskname', 'status']
								        })
									}]
								}]
							}]
						}]
					}]
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});