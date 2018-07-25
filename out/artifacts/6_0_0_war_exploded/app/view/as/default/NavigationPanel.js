Ext.define('erp.view.opensys.default.NavigationPanel', { 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.navigationpanel',
	region:'west',
	/*border: false,
	bodyBorder:false,*/
	id:'app-as-navigation',
	bodyCls:'cus-leftbg',
	initComponent: function() { 
		var me = this;
		//this.items=me.loadIconNavigation();
		Ext.applyIf(this,{
			width: 200,
			minWidth: 150,
			maxWidth: 400,
			split: false,
			collapsible: false,
			items: this.createView(),
			layout:{
				type: 'accordion',
				animate: true
			}
		}); 
		this.callParent(arguments); 
	},
	createView:function(parentId){
		var accordItems=new Array(),me=this;
		Ext.Ajax.request({
			url : basePath + 'opensys/getCurSysnavigation.action',
			method:'post',
			async:false,
			callback : function(options, success, response){
				var res = new Ext.decode(response.responseText);
				Ext.Array.each(res.data,function(item){
					var o=new Object(),data;
					o.title=item.cn_title;
					o.bodyCls='cus-leftbg';
					if(item.children){
						data=new Array();
						Ext.Array.each(item.children,function(child){
							data.push({
								title:child.cn_title,
								url:child.cn_url,
								sign:child.cn_id,
								icon:basePath+'resource/images/customer/'+child.cn_icon
							});
						});
						o.items=[Ext.create('widget.dataview',{
							autoScroll: true,
							tpl:[ '<tpl for=".">',
							      '<div class="ux-shortcut">',
							      '<div class="ux-shortcut-icon">',
							      '<img src="{icon}" title="{title}">',
							      '</div>',
							      '<span class="ux-shortcut-text">{title}</span>',
							      '</div>',
							      '</tpl>',
							      '<div class="x-clear"></div>'],
							itemSelector:'div.ux-shortcut',
							trackOver: true,
							selModel: {
								mode: 'SINGLE',
								listeners: {
									scope:me,
									selectionchange: me.onSelectionChange
								}
							},
							store: Ext.create('Ext.data.Store', {
								fields: [{name: 'title' },
								         {name:'url' },
								         {name:'icon' },
								         {name:'sign'}],
								data:data
							}),
						})];
					}
					accordItems.push(o);
				});
			}
		});
	
		return accordItems;
	},
/*	formatData:function(data){
		var formatData=new Array(),o=null;
		Ext.Array.each(data,function(item){
			o=new Object();
			o.title=item.cn_title;
			o.leaf=item.cn_isleaf==1;
			if(item.children.length>0){
				Ext.Array.each(item.children,)
			}
		});
		return formatData;
	},*/
	onSelectionChange: function(view,selects){
	   var tabPanel=this.ownerCt.down('centerTabPanel');
	   tabPanel.loadTab(selects[0]);
	}
});