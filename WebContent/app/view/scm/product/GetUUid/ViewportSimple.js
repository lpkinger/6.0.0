Ext.define('erp.view.scm.product.GetUUid.ViewportSimple',{ 
	extend: 'Ext.Viewport', 
	layout: 'border',
	cls: 'x-custom',
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region: 'north',
				items: [{
					xtype: 'form',
					layout : 'column',
					bodyStyle: {background: '#f0f0f1'},
					items: [{
						xtype: 'fieldcontainer',
						columnWidth: .85,
						layout: 'hbox',
						items: [{
							xtype: 'textfield',
							cls: 'x-form-item-medium',
							width: 300,
							id: 'orispecode',
							name: 'orispecode',
							emptyText:'输入原厂型号搜索',
							margin:'3 3 3 3',
							hidden:true
						},{
							xtype: 'button',
						    name: 'search',
							text: '搜索',
							scale  : 'medium',
							margin:'3 3 3 3',
							hidden:true
						},{		
							xtype: 'button',
						    name: 'reset',
							text: '重置',
							hidden:true,
							scale  : 'medium',
							margin:'3 3 3 3',
					    	handler: function(){
					    		Ext.getCmp('tree-panel').getTreeRootNode(0);
					    		Ext.getCmp('orispecode').setValue(null);
					    		Ext.getCmp('uuIdGrid').store.loadData([]);
					    		Ext.getCmp('choose').update({});
					    	}
						}]
					}]
				},{
					xtype: 'panel',
					id: 'choose',
					height: 30,
					data: {},
					tpl: Ext.create('Ext.XTemplate',
							'<ul class="breadcrumb">',
						        '<li><a href="#"><img src="' + basePath + 'resource/images/screens/home.png" alt="Home" class="home" /></a></li>',
						        '<tpl for="nodes">',
					        		'<li><a href="#" title="{.}">{.}</a></li>',
					        	'</tpl>',
						        '<tpl if="this.isEmpty(values.nodes)">',
							    	'<li><span>请先选择器件类目</span></li>',
						        '</tpl>',
						    '</ul>', {
						isEmpty: function(datas) {
							return datas ? datas.length == 0 : true;
						}
					})
				}]
			}, {
				xtype: 'prodb2ckindtree',
				region: 'center',
				width: 300,
				autoScroll: true				
			},
			]
		}); 
		me.callParent(arguments); 
	} 
});