Ext.define('erp.view.ma.update.EmpgridRight',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.empgridright',
	layout : 'fit',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    defaultdata:new Array(),
    filterEvevt:false,
    requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.plugin.CopyPasteMenu'],
    columns:[{align: "left",cls: "x-grid-header-1",dataIndex: "em_code",flex: 1,header: "员工编号",text: "员工编号",filter:{autoDim: true,dataIndex: 'em_code',
    					displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
    		 },{align: "left",cls: "x-grid-header-1",dataIndex: "em_name",flex: 1,header: "员工姓名",text: "员工姓名",filter:{autoDim: true,dataIndex: 'em_name',
    					displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
    		},{align: "left",cls: "x-grid-header-1",dataIndex: "em_defaulthsname",flex: 1,header: "岗位",text: "岗位",filter:{autoDim: true,dataIndex: 'em_defalthsname',
    					displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}
    		},{align: "left",cls: "x-grid-header-1",dataIndex: "em_defaultorname",flex: 1,header: "组织",text: "组织",filter:{autoDim: true,dataIndex: 'em_defaultorname',
    					displayField: "display",exactSearch: false,ignoreCase: false,queryMode: 'local',store: null,valueField: 'value',xtype:'textfield'}}],
    
   	store: Ext.create('Ext.data.Store', {
       					 fields:[{name: 'em_code',type: 'string'},{name: 'em_name',type: 'string'},
       					 		{name: 'em_defalthsname',type: 'string'},{name: 'em_defaultorname',type: 'string'}],
       					 data:[],
       					 listeners:{
	       					 'datachanged':function(){
	       					 	var grid=Ext.getCmp('selectgrid');
	       					 	var s1=new Array();
	       					 	if(grid){
	       					 		 Ext.each(this.data.items,function(d){
							           s1.push(d.data);
								    });
								    grid.defaultdata=s1;
								    parent.Ext.getCmp('empnames_').selecteddata=grid.defaultdata;
	       					 	}		 	
	       					 }
       					 }
       					 
	}),
    multiselected: new Array(),
    selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	ignoreRightMouseSelection : false,
	    	checkOnly: true,
			listeners:{}
	}),
	constructor: function(cfg) {
    	if(cfg) {
    		cfg.headerCt = cfg.headerCt || Ext.create("Ext.grid.header.Container", {
        		id: (cfg.id || this.id) + '-ct',
         	    forceFit: false,
                sortable: true,
                enableColumnMove:true,
                enableColumnResize:true,
                enableColumnHide: true
             });
        	cfg.plugins = cfg.plugins || [Ext.create('erp.view.core.grid.HeaderFilter'), Ext.create('erp.view.core.plugin.CopyPasteMenu')];
        	Ext.apply(this, cfg);
    	}
    	this.callParent(arguments);
     },
	initComponent : function(){
		this.getColumnsAndStore();
		this.callParent(arguments);
		var codes=parent.Ext.getCmp('emps_').value;
		if(codes!=''){
			var cond="'"+codes.replace(/#/g,"','")+"'";
			this.getData(cond);
		}else{
			data=new Array();
			for(var i=0;i<pageSize;i++){
				var o = new Object();
				data.push(o);
			}
			this.getStore().loadData(data);
		}
	},
	removeFromRight:function(){
		var gridR=Ext.getCmp('selectgrid');
		var remove=new Array(),s1=new Array();
		Ext.each(gridR.selModel.getSelection(),function(ss){
	           remove.push(ss.data);
	    });
	    Ext.each(gridR.store.data.items,function(ss){
	           s1.push(ss.data);
	    });
		Ext.each(remove,function(r){
	         if(Ext.Array.contains(s1,r)){
	            Ext.Array.remove(s1,r);
	         }
	    });
	    gridR.getStore().loadData(s1);
	},
	getData:function(cond){
		var me=this;
		var f=condition+"and em_code in("+cond+")";
		me.setLoading(true);
		Ext.Ajax.request({
        	url : basePath + 'ma/update/getEmpdbfindData.action',
        	method : 'post',
        	params : {
        		fields:'em_code,em_name,em_defaulthsname,em_defaultorname',
	   			condition: f,
	   			page: -1,
	   			pagesize: 0
	   		},
		    method : 'post',
		    callback : function(opt, s, res){
		       me.setLoading(false);
		       var r = new Ext.decode(res.responseText);
		       if(r.exceptionInfo){
		    		showError(r.exceptionInfo);return;
		    	} else if(r.success && r.data){
		    	var data = Ext.decode(r.data.replace(/,}/g, '}').replace(/,]/g, ']'));
		    		me.getStore().loadData(data);		    
		    	}
		    }
		});
	},
	RenderUtil: Ext.create('erp.util.RenderUtil'),
	getColumnsAndStore: function(c, d, g, s, callback){
		var me = this;
		me.reconfigure(me.store, me.columns);
	}
});