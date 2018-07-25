Ext.define('erp.view.common.DeskTop.CommonUsePortal', {
	extend: 'erp.view.common.DeskTop.Portlet',
	alias: 'widget.commonuseportal',
	title: '<div class="div-left">常用模块</div>',
	iconCls: 'main-news',
	id: 'bench_link',
	enableTools:true,
	pageCount:15,
	activeRefresh:true,
	initComponent : function() {
		var me = this;   
		Ext.apply(me,{
			items: [{
				xtype: 'gridpanel',
				id: 'link_grid',
				cls: 'custom-grid',
				columnLines : true,
				sortableColumns: false,
				enableColumnResize: false,
				enableColumnMove: false,
				store: Ext.create('Ext.data.Store', {
					fields:['cu_id', 'cu_description', 'cu_url', 'cu_count','cu_lock'],
					proxy: {
						type: 'ajax',
						url : basePath + 'common/getCommonUse.action',
						method : 'get',
						extraParams:{
							count:me.pageCount	
						},
						reader: {
							type: 'json',
							root: 'commonuse'
						}
					}, 
					autoLoad:true  
				}),
				bodyStyle: 'background: #f1f1f1;',
				columns: [{ 
					text: 'ID',  
					dataIndex: 'cu_id', 
					hidden: true
				},{ 
					text: '模块名',  
					dataIndex: 'cu_description', 
					flex: 1, 
					renderer: function(val, meta, record){
						var ht = '<a class="x-btn-link" onclick="openTable(' 
							+ record.data['cu_id'] + ",null,\'" + val + "\',\'" + record.data['cu_url'].replace(/\'/g, '\\\'') + "\',null,null,null,null,true)\">" + val + "</a>";
						return ht;
					}
				},{ 
					text: '链接', 
					dataIndex: 'cu_url', 
					hidden: true 
				},{ 
					text: '次数', 
					dataIndex: 'cu_count', 
					hidden: true
				},{
					xtype: 'actioncolumn',
					text: '操作', 
					flex: 0.25,
					items: [{
						icon:basePath + 'resource/images/16/lock_bg.png',
						tooltip: '锁定',
						iconCls:'',
						getClass: function(v, meta, rec) {
	                       if(rec.get('cu_lock')==1){
	                      		this.items[0].tooltip = '取消关注';
	     						return 'lock';
	                       }else{
	                       		this.items[0].tooltip = '关注';
		                        return 'lockopen';
	                       }
	                  	},
						handler: function(view, rowIndex, colIndex) {
							var rec = view.getStore().getAt(rowIndex);
							var type=0;
							if(rec.data.cu_lock==null||rec.data.cu_lock==0){
							 	type=1;
							}else{
								type=0;
							}
							view.ownerCt.lockCommonUse(rec.get('cu_id'), type);
						}
					}/*,{
						icon: basePath + 'resource/images/16/up.png',
						tooltip: '上',
						handler: function(view, rowIndex, colIndex) {
							var rec = view.getStore().getAt(rowIndex);
							view.ownerCt.updateCommonUse(rec.get('cu_id'), 1);
						}
					},{
						icon: basePath + 'resource/images/16/down.png',
						tooltip: '下',
						handler: function(view, rowIndex, colIndex) {
							var rec = view.getStore().getAt(rowIndex);
							view.ownerCt.updateCommonUse(rec.get('cu_id'), -1);
						}
					}*/,{
						icon: basePath + 'resource/images/upgrade/bluegray/mainicon/close2.png',
						iconCls: 'cu-delete',
						tooltip: '删除',
						handler: function(view, rowIndex, colIndex) {
							var rec = view.getStore().getAt(rowIndex);
							view.ownerCt.deleteCommonUse(rec.get('cu_id'));
						}
					}]
				}],
				lockCommonUse : function(id, t) {
					var g = this;
					g.setLoading(true);
					Ext.Ajax.request({
						url : basePath + 'common/lockCommonUse.action',
						params : {
							_noc : 1,
							id : id,
							type : t,
							count:15
						},
						callback : function(o, s, r) {
							g.setLoading(false);
							var rs = Ext.decode(r.responseText);
							if (rs.commonuse) {
								g.store.loadData(rs.commonuse);
							}
						}
					});
				},
				updateCommonUse : function(id, t) {
					var g = this;
					g.setLoading(true);
					Ext.Ajax.request({
						url : basePath + 'common/updateCommonUse.action',
						params : {
							_noc : 1,
							id : id,
							type : t,
							count:15
						},
						callback : function(o, s, r) {
							g.setLoading(false);
							var rs = Ext.decode(r.responseText);
							if (rs.commonuse) {
								g.store.loadData(rs.commonuse);
							}
						}
					});
				},
				deleteCommonUse :function(id){
					var g = this;
					Ext.Ajax.request({
						url : basePath + 'common/deleteCommonUse.action',
						params: {
							id: id
						},
						method : 'post',
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.exception || res.exceptionInfo){
								showError(res.exceptionInfo);
								return;
							}
							Ext.Array.each(g.store.data.items, function(item){
								if(item.data['cu_id'] == id){
									g.store.remove(item);
									return false;
								}
							});
						}
					});					
				}
			}] 
		});
		this.callParent();
	},
	_dorefresh:function(panel){
		var gridpanel=panel.down('gridpanel');
		gridpanel.getStore().load();
		//解决刷新时 panel丢失高度 导致panel显示出错
		if(!gridpanel._firstWidth){
			gridpanel._firstWidth = gridpanel.preLayoutSize.width
		}
		if(gridpanel._firstWidth!=gridpanel.preLayoutSize.width){
			gridpanel.setWidth(gridpanel._firstWidth);
		}
	},
	getMore:function(){
		openTable(null,null,'常用模块',"jsps/common/commonuse.jsp",null,null);				
	}
});
