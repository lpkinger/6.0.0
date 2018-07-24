Ext.define('erp.view.scm.purchase.moreInviteInfo.InviteGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.InformationGrid',
	constructor: function(cfg) {
		if(cfg) {
			cfg.plugins = cfg.plugins || [Ext.create('erp.view.core.plugin.CopyPasteMenu')];
			Ext.apply(this, cfg);
		}
		this.callParent(arguments);
	},
	layout:'fit',
	columns : [],
	region: 'center',
	forceFit: true,
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	listeners: {
		'activate':function(grid){
			grid.getStore().load({
				scope: this,
			    callback: function(records, operation, success) {
			    }
			});
		}
	},
	initComponent : function() {
		var me = this;
		me.store = Ext.create('Ext.data.Store',{
					fields:me.myfields,
					storeId:me._state==null?"allInviteData":me._state+"InviteData",
					pageSize : pageSize,
					proxy: {
						type: 'ajax',
						url : basePath+'ac/invitationsRecord.action',
						method : 'GET',
						extraParams:{
							_state:me._state,
							caller:'invitationsRecord'
						},
						timeout:180000,
						reader: {
							type: 'json',
							root: 'data',
							totalProperty:'count'
						}
					},
					autoLoad:false ,
					listeners : {
						beforeload : function() {
							var inviteTab = Ext.getCmp('inviteTab');
							var grid = inviteTab.getActiveTab();
					    	var keyword = Ext.getCmp('keySearch');
					    	if(keyword){
					    		Ext.apply(grid.getStore().proxy.extraParams, {
					    			keyword: keyword.value
								});
					    	}
						},
						'datachanged':function(){
							Ext.getCmp('moreInviteInfoViews').resetTabTitle();
						}
					}
				});
		me.dockedItems = [{
			xtype : 'pagingtoolbar',
			dock : 'bottom',
			displayInfo : true,
			store : me.store,
			displayMsg : "显示{0}-{1}条数据，共{2}条数据",
			beforePageText : '第',
			afterPageText : '页,共{0}页',
			dataCount:0,
			items:['-',
				{
					id:me._state+'datalistexport',
					name: 'export',
					tooltip: $I18N.common.button.erpExportButton,
					iconCls: 'x-button-icon-excel',
					cls: 'x-btn-tb',
					width: 24,
					hidden:getUrlParam('_noexport')==-1,
					handler : function(i) {
						var me = i.ownerCt,
						grid = me.ownerCt,
						title = grid.title.substr(0,grid.title.indexOf("("));
						grid.exportGrid(grid,title);
					}
				},'-',{
					itemId: 'close',
					tooltip:$I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
					width: 24,
					cls: 'x-btn-tb',
					handler: function(){
						var main = parent.Ext.getCmp("content-panel");
						if(!main){
							var main = parent.parent.Ext.getCmp("content-panel");
						}
						if(main)
							main.getActiveTab().close();
						else if(typeof parentDoc !== 'undefined' && parentDoc) {
							var doc = parent.Ext.getCmp(parentDoc);
							if(doc) {
								doc.fireEvent('close', doc);
							}
						}
					}
				}]
		}];
		me.exportGrid = function(grid,title){
			var id = grid.id.replace("Grid","");
			var dataCount = grid.dockedItems.dataCount;
			var columns = (grid.columns && grid.columns.length > 0) ? 
					grid.columns : grid.headerCt.getGridColumns(),
					cm = new Array(), datas = new Array(), gf = grid.store.groupField;
			Ext.Array.each(columns, function(c){
				if(c.dataIndex == gf || (!c.hidden && (c.width > 0 || c.flex > 0) && !c.isCheckerHd)) {
					if((c.items && c.items.length > 0) || (c.columns && c.columns.length > 0)) {
						var items = (c.items && c.items.items) || c.columns;
						Ext.Array.each(items, function(item){
							if(!item.hidden) {
								var text = (Ext.isEmpty(c.text) ? ' ' : c.text.replace(/<br>/g, '\n')) + '(' + item.text.replace(/<br>/g, '\n') + ')';
								cm.push({
									text: text, 
									dataIndex: item.dataIndex, 
									width: item.width, 
									xtype: item.xtype, 
									format: item.format, 
									locked: item.locked, 
									summary: false,
									group: item.dataIndex == gf
								});		
							}
						});
					} else {
						// ext4.2 GroupHeader
						var text = ((c.ownerCt && c.ownerCt.isGroupHeader ? '(' +c.ownerCt.text + ')' : '') + (c.text || '')).replace(/<br>/g, '\n');
						cm.push({
							text: text, 
							dataIndex: c.dataIndex, 
							width: (c.dataIndex == gf ? 100 : c.width), 
							xtype: c.xtype, format: c.format, 
							locked: c.locked, 
							summary: false,
							group: c.dataIndex == gf,
							logic: c.logic
						});
					}
				}
			});
			if (!Ext.fly('ext-grid-excel')) {  
				var frm = document.createElement('form');  
				frm.id = 'ext-grid-excel';  
				frm.name = frm.id;  
				frm.className = 'x-hidden';
				document.body.appendChild(frm);  
			}
			Ext.Ajax.request({
				disableCaching: true ,  
				url: basePath + 'ac/excel/exportInviteGrid.xls',
				method: 'post',
				form: Ext.fly('ext-grid-excel'),
				isUpload: true,
				timeout: 180000, 
				params: {
					id: id,
					dataCount:dataCount,
					columns: unescape(Ext.encode(cm).replace(/\\u/g,"%u")),
					title: unescape(title.replace(/\\u/g,"%u").replace(/,/g," "))
				}
			});
		},
		this.callParent(arguments);
	}
});