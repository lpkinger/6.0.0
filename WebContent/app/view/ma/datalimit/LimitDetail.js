Ext.define('erp.view.ma.datalimit.LimitDetail',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.limitdetailgrid',
	id:'limitdetail',
	layout : 'fit',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store:Ext.create('Ext.data.Store',{
    	fields:['id_','code_','desc_','see_','update_','delete_'],
    	proxy: {
			type: 'ajax',
			url : basePath+'/ma/datalimit/getLimitDetails.action',
			reader: {
				type: 'json'
			}
		}
    }),
   // forceFit:true,
    keyField:'id_',
    initcount_:0,
    selModel: Ext.create('Ext.selection.CheckboxModel',{
		checkOnly : true,
		ignoreRightMouseSelection : false,
		getEditor: function(){
			return null;
		},
		onHeaderClick: function(headerCt, header, e) {
			if (header.isCheckerHd) {
				e.stopEvent();
				var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
				if (isChecked && this.getSelection().length > 0) {
					this.deselectAll(true);
				} else {
					this.selectAll(true);
					this.view.ownerCt.selectall = true;
				}
			}
		}
	}),
	plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1})],
    columns: [{
    	text:'ID',
    	dataIndex:'id_',
    	width:0
    },{
      text:'代码',
      dataIndex:'code_',
      sortable:false,
      flex:1
    },{
      text:'名称',
      dataIndex:'desc_',
      sortable:false,
      flex:1
    },{
      text:'查询权',
      dataIndex:'see_',
      sortable:false,
      flex:0.33,
      xtype:'checkcolumn',
      editor:{
    	 xtype:'checkbox',
    	 cls: 'x-grid-checkheader-editor'
      }
    },{
      text:'修改权',
      dataIndex:'update_',
      xtype:'checkcolumn',
      sortable:false,
      flex:0.33,
      editor:{
    	 xtype:'checkbox',
    	 value:0,
    	 cls: 'x-grid-checkheader-editor'
      }
    },{
      text:'删除权',
      dataIndex:'delete_',
      xtype:'checkcolumn',
      sortable:false,
      flex:0.33,      
      editor:{
    	 xtype:'checkbox',
    	 cls: 'x-grid-checkheader-editor',
    	 value:0
      }
    }],
    GridUtil: Ext.create('erp.util.GridUtil'),
	initComponent : function(){
		this.callParent(arguments);
	},
	insertRecords:function(records){
		var insertRecords=new Array(),store=this.getStore(),maxIndex=store.getTotalCount();
		Ext.Array.each(records,function(item){
			if(store.find("code_",item.get('CODE_'))==-1){
				insertRecords.push({
					code_:item.data.CODE_,
					desc_:item.data.DESC_,
					see_:1,
					update_:0,
					delete_:0
				});
			}
		});
		store.insert(maxIndex,insertRecords);
	},
	getChange: function(){
		var grid = this,items = grid.store.data.items,key = grid.keyField,
		added = new Array(),updated = new Array(),d = null;
		Ext.each(items, function(item){
			d = item.data;
			d['see_']=d['see_'] ? 1 : 0;
			d['update_']=d['update_'] ? 1 : 0;
			d['delete_']=d['delete_'] ? 1 : 0;
			if(d[key] !=0 && d[key]!=null && d[key]!=" "){
				if(item.dirty)
				updated.push(Ext.JSON.encode(d));
			}else{
				added.push(Ext.JSON.encode(d));
			} 
		});
		return {
			added: added,
			updated: updated
		};
	}
});