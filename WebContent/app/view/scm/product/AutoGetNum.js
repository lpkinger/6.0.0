Ext.define('erp.view.scm.product.AutoGetNum',{ 
	extend: 'Ext.Viewport', 
	layout: 'border',
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region: 'north',
				items: [{
					xtype: 'panel',
					id: 'choose',
					height: 30,
					hidden: true,
					tpl: Ext.create('Ext.XTemplate',
							'<ul class="breadcrumb">',
						        '<li><a href="#"><img src="' + basePath + 'resource/images/screens/home.png" alt="Home" class="home" /></a></li>',
						        '<tpl for="nodes">',
						        	'<li><a href="#" title="{.}">{.}</a></li>',
						        '</tpl>',
						    '</ul>'
					)
				},{
					xtype: 'form',
					layout : 'column',
					height: 36,
					bodyStyle: {background: 'rgb(224, 224, 224)'},
					items: [{
						margin:'5 10 2 0',
						fieldLabel: '编号',
						xtype: 'textfield',
						fieldStyle : "background:#FFFAFA;color:#515151;",
						readOnly: true,
						labelWidth : 40,
						columnWidth: .5,
						id: 'auto_code',
						name: 'auto_code'	
					},{
						margin:'5 10 2 0',
						height:24,
						xtype: 'button',
						columnWidth: .15,
						iconCls: 'x-button-icon-code',
					    	cls: 'x-btn-gray-1',
					    	name: 'code',
						text: '生成编号'
					},{
						margin:'5 10 2 0',
						height:24,
						xtype: 'button',
						id: 'confirm',
						name: 'confirm',
						columnWidth: .1,
						text : $I18N.common.button.erpConfirmButton,
					    	iconCls: 'x-button-icon-confirm',
					    	cls: 'x-btn-gray-1'	
					},{
						margin:'5 0 2 0',
						height:24,
						xtype: 'button',
						name: 'close',
						columnWidth: .1,
						text : $I18N.common.button.erpCloseButton,
					    	iconCls: 'x-button-icon-close',
					    	cls: 'x-btn-gray-1'
					}]
				}]
			}, {
				xtype: 'prodkindtree',
				region: 'center',
				autoScroll: true
			}]
		}); 
		me.callParent(arguments); 
	} 
});