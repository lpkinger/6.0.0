Ext.define('erp.view.pm.bom.ECRChangeGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpECRChangeGridPanel',
	requires: ['erp.view.core.trigger.TextAreaTrigger', 'erp.view.core.toolbar.Toolbar'],
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    store:[],
    columns:[],
    id:'ecrchangegrid',
    detno:'detno',
    bodyStyle: 'background-color:#f1f1f1;',
    bbar: {xtype: 'erpToolbar', enableAdd: true, enableDelete: true, enableCopy: false, enablePaste: false, enableUp: false, enableDown: false},
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }), Ext.create('erp.view.core.grid.HeaderFilter'), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	initComponent : function(){ 
	    this.GridUtil = Ext.create('erp.util.GridUtil');
	    var columns=this.getColumns(this);
	    this.columns=columns;
	    this.store=this.getGridStore(this);
	    this.callParent(arguments);
	},
	getGridStore:function(grid){
		var store=[]; var data=new Array();
		if(grid.griddata['tempb']!='' || grid.griddata['tempc']!=''){
		    	var tempb=grid.griddata['tempb'].split("#");
		    	var tempc=grid.griddata['tempc'].split("#");
		   
		    	Ext.Array.each(tempb,function(item,index){
		    		data.push({
		    			detno:index+1,
		    			tempb:item,
		    			tempc:tempc[index]
		    		});
		    	});
		  }else {
			  for(var i=0;i<10;i++){
				  data.push({
					detno:i+1
				  });
			  }
		  }
		store=Ext.create('Ext.data.Store',{
			fields:['detno','tempb','tempc'],
			data:data
		});
		return store;
	},
	setRenderer: function(column){
		var grid = this;
		if(!column.haveRendered && column.renderer != null && column.renderer != ""){
    		var renderName = column.renderer;
    		if(contains(column.renderer, ':', true)){
    			var args = new Array();
    			Ext.each(column.renderer.split(':'), function(a, index){
    				if(index == 0){
    					renderName = a;
    				} else {
    					args.push(a);
    				}
    			});
    			if(!grid.RenderUtil.args[renderName]){
    				grid.RenderUtil.args[renderName] = new Object();
    			}
    			grid.RenderUtil.args[renderName][column.dataIndex] = args;
    		}
    		column.renderer = grid.RenderUtil[renderName];
    		column.haveRendered = true;
    	}
	},
	getReturnData:function(grid){
		var s = grid.getStore().data.items;
		var tempb="",tempc="",obj=new Object();
		for(var i=0;i<s.length;i++){
			var data = s[i].data;
			if(data['tempb']!='' || data['tempc']!=''){
				tempb+=data['tempb']+'#';
				tempc+=data['tempc']+'#';				
			}
		}
		if(tempb!=''){
            obj.tempb=tempb.substring(0,tempb.length-1);
			obj.tempc=tempc.substring(0,tempc.length-1);
		}
		return obj;
	},
	listeners: {
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		},
		itemclick: function(selModel, record){
			var changegrid=Ext.getCmp('ecrchangegrid');
			if(!changegrid.readOnly){
				var items=changegrid.dockedItems.items[2].items.items;
				items[3].setDisabled(false);
				items[2].setDisabled(false);
			}
		}
	},
	getColumns:function(grid){
		return [{
		  cls:'x-grid-header-1',
		  text:'序号',
		  dataIndex:'detno',
		  flex:0.1,
		  readOnly:grid.readOnly,
		  renderer:function(val, meta) {
			   meta.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
			   return val;
		  }
		},{
		  cls : "x-grid-header-1",
		  text: '变  更  前',
		  dataIndex: 'tempb',
		  flex: 1,
		  readOnly:grid.readOnly,		 
		  editor: {
				format:'',
				xtype: 'textareatrigger'
		  },
		  renderer: function(val, meta, record){
				if(!val){
					val="";
				}
				val=val.replace(/[ ]/g,"&nbsp;");
				return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
				'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';						  
		  }
	  },{
		 cls : "x-grid-header-1",
		 text: '变  更  后',
		 dataIndex: 'tempc',
		 flex: 1,
		 readOnly:grid.readOnly,
		 editor: {
				format:'',
				xtype: 'textareatrigger'
		 },
		 renderer: function(val, meta, record){
				if(!val){
					val="";
				}
				val=val.replace(/[ ]/g,"&nbsp;");
				return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
				'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';						  
		 }
	   }];
	}
});