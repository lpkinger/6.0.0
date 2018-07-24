/**
 * 报表查询界面
 */
Ext.define('erp.view.common.reportsQuery.reportsGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.reportsGrid',
	requires: ['erp.view.core.toolbar.Toolbar'],
	region: 'center',
	layout : 'fit',
	id: 'reportsGrid', 
	emptyText : '无数据',
	fields: ['ID','NAME','TYPE','USING','URL'],
	queryType:'mystore',//根据类型查询不同的store
/*	viewConfig:{
		 getRowClass: function(record, rowIndex, rowParams, store){	
		 	return (record.get(1)=='2') ? 'default' : null;		
		 }
	},*/
	tbar:{
		xtype:'toolbar',
		height:50,
		cls:'x-top-bar',
		items:[{
			id:'myQuery',
			cls:'x-top-btn',
			margin:'0 5 0 10',
			text:'常用报表',
			listeners:{
				afterrender : function(b){
					var grid = Ext.getCmp('reportsGrid');
					grid.store.load();
					b.el.dom.getElementsByClassName('x-btn-inner')[0].classList.add('x-top-color');
					b.el.dom.classList.add('x-top-line');
				},
				click : function(b){
					var othbrn = Ext.getCmp('query');
					othbrn.el.dom.getElementsByClassName('x-btn-inner')[0].classList.remove('x-top-color');
					othbrn.el.dom.classList.remove('x-top-line');
					var report = Ext.getCmp('report');
					report.el.dom.getElementsByClassName('x-btn-inner')[0].classList.remove('x-top-color');
					report.el.dom.classList.remove('x-top-line');
					b.el.dom.getElementsByClassName('x-btn-inner')[0].classList.add('x-top-color');
					b.el.dom.classList.add('x-top-line');
					//重载store
					var grid = Ext.getCmp('reportsGrid');					
					if(grid.store&&grid.queryType!='mystore'){
						grid.queryType = 'mystore';
						var newStore = grid.getMyStore(grid.fields);
						grid.store.removeAll();
						grid.reconfigure(newStore,grid.column);
						grid.store.load();	
						var f = Ext.getCmp('searchfield')
						grid.store.clearFilter();
						if(f.value == '' || f.value == null){
							return;
						}else{
							grid.store.filter([
							    {filterFn: function(item) { return contains(item.data['NAME'],f.value,true);  }}
							]);
						}
					}
				}
			}
		},'-',{
			id:'report',
			cls:'x-top-btn',
			margin:'0 5 0 10',
			text:'报表',
			listeners:{
				click : function(b){
					var othbrn = Ext.getCmp('query');
					othbrn.el.dom.getElementsByClassName('x-btn-inner')[0].classList.remove('x-top-color');
					othbrn.el.dom.classList.remove('x-top-line');
					var myQuery = Ext.getCmp('myQuery');
					myQuery.el.dom.getElementsByClassName('x-btn-inner')[0].classList.remove('x-top-color');
					myQuery.el.dom.classList.remove('x-top-line');
					b.el.dom.getElementsByClassName('x-btn-inner')[0].classList.add('x-top-color');
					b.el.dom.classList.add('x-top-line');
					//重载store
					var grid = Ext.getCmp('reportsGrid');	
					if(grid.store&&grid.queryType!='report'){
						grid.queryType = 'report';
						var newStore = grid.getReportStore(grid.fields);
						grid.store.removeAll();
						grid.reconfigure(newStore,grid.column);
						grid.store.load();	
						var f = Ext.getCmp('searchfield')
						grid.store.clearFilter();
						if(f.value == '' || f.value == null){
							return;
						}else{
							grid.store.filter([
							    {filterFn: function(item) { 
							    	return contains(item.data['NAME'],f.value,true);  
							    }}
							]);
						}
					}
				}
			}
		},'-',{
			id:'query',
			cls:'x-top-btn',
			margin:'0 30 0 5',
			text:'查询',
			listeners:{
				click : function(b){
					var othbrn = Ext.getCmp('report');
					othbrn.el.dom.getElementsByClassName('x-btn-inner')[0].classList.remove('x-top-color');
					othbrn.el.dom.classList.remove('x-top-line');
					var myQuery = Ext.getCmp('myQuery');
					myQuery.el.dom.getElementsByClassName('x-btn-inner')[0].classList.remove('x-top-color');
					myQuery.el.dom.classList.remove('x-top-line');
					b.el.dom.getElementsByClassName('x-btn-inner')[0].classList.add('x-top-color');
					b.el.dom.classList.add('x-top-line');
					//重载store
					var grid = Ext.getCmp('reportsGrid');					
					if(grid.store&&grid.queryType!='query'){
						grid.queryType = 'query';
						var newStore = grid.getQueryStore(grid.fields);
						grid.store.removeAll();
						grid.reconfigure(newStore,grid.column);
						grid.store.load();
						var f = Ext.getCmp('searchfield')
						grid.store.clearFilter();
						if(f.value == '' || f.value == null){
							return;
						}else{
							grid.store.filter([
							    {filterFn: function(item) { 
							    	return contains(item.data['NAME'],f.value,true);  
							    }}
							]);
						}
					}
				}
			}
		},{
			width:215,
			id:'searchfield',
	        xtype: 'searchfield',
	        cls: 'search-field',
	        emptyText:'搜索方案名称',
	        fieldStyle:'height: 22px;color: rgb(0, 0, 0) !important;background: #fff;border-color: rgb(180, 180, 180);border-radius: 15px;',
	        onTriggerClick: function(){
	        	var f = this;
	        	var store = Ext.getCmp('reportsGrid').store;
				if(store){
					store.clearFilter();
					if(f.value == '' || f.value == null){
						return;
					}else{
						store.filterBy(function(record) { 
					  	    return contains(record.data['NAME'],f.value,true); 
		                });
					}
				}
	        }
		},'->',{
			cls:'x-displayfield-tip',
			margin:'0 20 0 0',
			xtype:'displayfield',
			value:'注：在报表或查询界面可以关注节点，会在常用报表中体现'
		}]			
	},
	emptyText : ' ',
	columnLines : true,
	autoScroll : true, 
	store: [],	
	columns:[{
		text:'ID',
		cls:'report-col',
		flex:0.15,
		dataIndex:'ID',
		hidden:true
	},{
		text:'分类',
		cls:'report-col',
		flex:0.15,
		dataIndex:'TYPE'
	},{
		text:'方案名称',
		dataIndex:'NAME',
		cls:'report-col',
		flex:0.35
	},{
		text:'访问',
		dataIndex:'URL',
		cls:'report-col',
		flex:0.15,
		align: 'center',
		renderer: function (value, metaData, record) {
			var grid = Ext.getCmp('reportsGrid');
				return Ext.String.format('<a href="javascript:openUrl2(\'{0}\',\'{1}\');" target="_blank">查询</a>',
						record.get('URL'),record.get('NAME')
				);
        }
	},{
		xtype: 'actioncolumn',
		text: '添加常用报表', 
		align : 'center',
		flex: 0.25,
		items: [{
			icon:basePath + 'resource/images/16/lock_bg.png',
			tooltip: '锁定',
			iconCls:'',
			getClass: function(v, meta, rec) {
               if(rec.get('USING')=='使用'){
              		this.items[0].tooltip = '取消关注';
					return 'x-grid-checkcolumn-checked';
               }else{
               		this.items[0].tooltip = '关注';
                    return 'x-grid-checkcolumn';
               }
          	},
			handler: function(view, rowIndex, colIndex) {
				var rec = view.getStore().getAt(rowIndex);
				var type=0;
				if(rec.data.USING==null){
				 	type=1;
				}else{
					type=0;
				}
				var res = view.ownerCt.changeReports(rec.get('ID'), type);
				if(!res){
					showMessage('添加失败，程序出错');
					return false;
				}
				//设置值
				rec.set('USING',type==1?'使用':null)
			}
		}]
	}],
	padding:'0 10 25 10',
	bodyStyle: 'background-color:#f1f1f1;',
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	dbfinds: [],
	caller: null,
	condition: null,
	gridCondition:null,
	initComponent : function(){
		var me = this;				
		Ext.apply(this,{
			store:[]	
		});
		if(title){
			var tab = parent.Ext.getCmp('content-panel').getActiveTab()
            tab.setTitle(title + '查询');
		}
		this.callParent(arguments);
	},
	listeners:{
		afterrender:function(g){
			var newStore = g.getMyStore(g.fields);
			g.reconfigure(newStore,g.column);
			g.store.load();
		}
	},
	getMyStore:function(fields){
		var me=this;
		return Ext.create('Ext.data.Store',{
			fields:fields,
			proxy: {
				type: 'ajax',
				url : basePath + 'common/desktop/newStyle/myStore.action',
				method : 'GET',
				extraParams:{
					count:me.pageCount
				},
				reader: {
					type: 'json',
					root: 'data'
				}
			}, 
			autoLoad:false  
		});
	},
	getReportStore:function(fields){
		var me=this;
		return Ext.create('Ext.data.Store',{
			fields:fields,
			proxy: {
				type: 'ajax',
				url : basePath + 'common/desktop/newStyle/reports.action',
				method : 'GET',
				extraParams:{
					count:me.pageCount,
					code:code
				},
				reader: {
					type: 'json',
					root: 'data'
				}
			}, 
			autoLoad:false  
		});
	},
	getQueryStore:function(fields){
		var me=this;
		return Ext.create('Ext.data.Store',{
			fields:fields,
			proxy: {
				type: 'ajax',
				url : basePath + 'common/desktop/newStyle/querys.action',
				method : 'GET',
				extraParams:{
					count:me.pageCount,
					code:code
				},
				reader: {
					type: 'json',
					root: 'data'
				}
			}, 
			autoLoad:false
		});
	},
	contains: function(string, substr, isIgnoreCase){
		if (string == null || substr == null) return false;
		if (isIgnoreCase === undefined || isIgnoreCase === true) {
			string = string.toLowerCase();
			substr = substr.toLowerCase();
		}
		return string.indexOf(substr) > -1;
	},
	changeReports:function(sn_id,type){
		var result;
		Ext.Ajax.request({
			async:false,
			url : basePath + 'common/desktop/newStyle/changeReports.action',
			params: {
				sn_id:sn_id,
				type:type
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
					result = false
				}else{
					result = true
				}
			}
		});
		return result;
	}
});