Ext.define('erp.view.scm.product.GetUUid.Viewport',{ 
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
							margin:'3 3 3 3'
						},{
							xtype: 'button',
						    name: 'search',
							text: '搜索',
							scale  : 'medium',
							margin:'3 3 3 3'
						},{		
							xtype: 'button',
						    name: 'reset',
							text: '重置',
							scale  : 'medium',
							margin:'3 3 3 3',
					    	handler: function(){
					    		Ext.getCmp('tree-panel').getTreeRootNode(0);
					    		Ext.getCmp('orispecode').setValue(null);
					    		Ext.getCmp('uuIdGrid').store.loadData([]);
					    		Ext.getCmp('choose').update({});
					    	}
						}]
					},{
					   xtype : 'label',
					   id : 'getpage',
					   name : 'getpage',
					   columnWidth: .15,
					   margin:'10 3 3 3',
					   html: '<a href=# style="font-size:14px;"> 没找到标准器件？ </a>'  ,
					   height:28,
					   listeners: {
						  render : function() {//渲染后添加click事件
					         Ext.fly(this.el).on('click',
					              function(e, t) {
					              	window.open(basePath+"/b2b/ucloudUrl_token.action?url=/vendor_erp%23/component/apply&b2cUrl=&accountUrl=");
									/*Ext.Ajax.request({//拿到tree数据
							        	url : basePath + 'scm/product/getPageAccess.action',
							        	async: false,
							        	callback : function(options,success,response){
							        		var res = new Ext.decode(response.responseText);
							        	    if(res.exceptionInfo){
				    			   				showError(res.exceptionInfo);
							        	    }else{
							        	    	window.open(res.path+'/api/webpage?access_token='+res.access_token+'&redirect_page=product#/componentEdit_T/detail/create/');
							        	    }
							        	}
							        });*/
					             }
					           );
					       }
					   }
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
				region: 'west',
				width: 300,
				autoScroll: true				
			},{
				xtype:'erpComponentGrid',
				region: 'center',
				split: true				
			}]
		}); 
		me.callParent(arguments); 
	} 
});