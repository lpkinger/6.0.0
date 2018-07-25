Ext.define('erp.view.sys.NavigationView',{
	extend: 'Ext.view.View', 
	alias: 'widget.navigationview', 
	overItemCls: 'x-view-over',
    trackOver: true,
    border:false,
	autoShow: true, 
	title:'导航',
	bodyStyle:'background-color:red;',
	itemSelector:'div.ux-desktop-shortcut',
	style: {
		position: 'absolute'
	},
	x: 0, y: 0,
	store: Ext.create('Ext.data.Store', {
		 fields: [{ name: 'name' },
		          { name: 'iconCls' },
		          { name: 'module' }],
        data: [
            { name: '初始进度', iconCls: 'grid-shortcut', module: 'progress' },
            { name: '数据导入', iconCls: 'notepad-shortcut', module: 'import' },
            { name: '数据检查', iconCls: 'cpu-shortcut', module: 'systemstatus'}
        ]
    }),
	tpl: [
	      '<tpl for=".">',
	      '<div class="ux-desktop-shortcut" id="{name}-shortcut">',
	      '<div class="ux-desktop-shortcut-icon {iconCls}">',
	      '<img src="',Ext.BLANK_IMAGE_URL,'" title="{name}">',
	      '</div>',
	      '<span class="ux-desktop-shortcut-text">{name}</span>',
	      '</div>',
	      '</tpl>',
	      '<div class="x-clear"></div>'
	      ],
	      initComponent : function(){ 
	    	  this.callParent(arguments);
	      }
});