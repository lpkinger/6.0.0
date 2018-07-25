Ext.define('erp.view.ma.logic.masterManage',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 		
		var me = this; 
		Ext.apply(me, { 		
			items: [{ 						   
					xtype: "panel",
					autoShow: true,	
					bodyStyle:'background:#ffffff',
					title:'禁用/启用账套(√为已启用帐套,*标记为主账套)',
					autoScroll:true,
					id:'master',					
			    	bbar: {cls: 'singleWindowBar',items:['->',{xtype: 'erpConfirmButton'},{ xtype: 'erpCloseButton'},'->']}
			}] 
		}); 
		me.callParent(arguments); 
		this.show();		
		this.showMasters();
	},
	showMasters: function() {
		var me=this;
    	me.GridUtil = Ext.create('erp.util.GridUtil');
		var store;
		Ext.Ajax.request({
		    async: false,
			url : basePath + 'common/getMasters.action',
			method : 'post',
			callback : function(opt,s,r){
				var res=new Ext.decode(r.responseText);   														
				if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
				else{
					store=res;
				}
			}
		});
		Ext.define('Master', {
		    extend: 'Ext.data.Model',
		    fields: [
		        {name: 'ma_function', type: 'string'},
		        {name: 'ma_enable',  type: 'bool'},
		        {name: 'ma_id',type: 'int'}
		    ]
		});
		var myStore = Ext.create('Ext.data.Store', {
		    model: 'Master',
		    data:store.masters,
		    autoLoad: true
		});
		var form=Ext.getCmp('master');
			form.add({
		        xtype: 'grid',  
		        store:myStore,
		        id:'mastergrid',
		        overflow:'auto',
		        columnLines:true,
		        autoScroll:true, 
		    	plugins: Ext.create('Ext.grid.plugin.CellEditing'),
		        columns: [
		                     { header: '账套名称', dataIndex: 'ma_function',flex: 5 ,editor:{allowBlank:false},
		                       renderer: function(value,f,m){
		                    	        if (m.data.ma_name ==store.defaultSob) {
		                    	            return (value+"*");
		                    	        }else{
		                    	        	 return value;
		                    	        }
		                    	    }},
		                     { header: '是否启用',dataIndex: 'ma_enable',flex: 1,xtype:'checkcolumn',
		                    	     headerCheckable : false,
		                    	     readOnly : false,
		                    	     cls : "x-grid-header-1", 
		                    	     renderer:function(value,f,m){
		                    		 var cssPrefix = Ext.baseCSSPrefix,
		                             cls = [cssPrefix + 'grid-checkheader'];
		                    		 if (m.data.ma_name ==store.defaultSob) {
		                    			return null;
		                    		 }
		                    		 if (value) {
				                             cls.push(cssPrefix + 'grid-checkheader-checked');
				                     }
		                         return '<div class="' + cls.join(' ') + '" >&#160;</div>';
		                    	 }},
		                     { header:'', hidden:true,dataIndex:'ma_id'},
		                     { header:'', hidden:true,dataIndex:'ma_name'}
		                  ],
		                  listeners:{
		                	  afterrender:function(){
		                		  me.GridUtil.updateFormPosition(form);
		                	  }
		                  }
		      });
			
			
	}
});