Ext.QuickTips.init();
Ext.define('erp.controller.plm.test.Buganalyse', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'plm.test.Buganalyse','plm.project.ProjectTreePanel','common.datalist.Toolbar','plm.test.AnalyseTestGrid','plm.test.AnalyseHandGrid','core.form.ConDateField',
	       'plm.project.AnalyseForm'
	       ],
	       init:function(){
	    	   var me=this;
	    	   var menu =null;
	    	   this.control({ 
	    		   'erpProjectTreePanel': {
	    			   itemmousedown: function(selModel, record){
	    				   if(record.get('leaf')){	
	    					   var id=record.get('id');
	    					   condition=" cl_prjplanid='"+id+"'";
	    					   var testgrid=Ext.getCmp('test');
		    				   var handgrid=Ext.getCmp('hand');
		    				   testgrid.getColumnsAndStore(condition);
		    				   handgrid.getColumnsAndStore(condition);
	    				   }	
	    			   },
	    			   afterrender:function(tree){

	    			   }
	    		   },
	    		   'datepicker':{
	    			   afterrender:function(picker){

	    			   }
	    		   },
	    		   'condatefield':{
	    			   afterrender:function(field){
	    				   Ext.getCmp('recorddate_to').setValue(new Date());
	    				   Ext.getCmp('recorddate_from').setValue('2012-04-01');
	    			   }
	    		   },
	    		  /** 'datefield[name=recorddate_from]':{
	    			   change:function(field){
	    				   var start=Ext.getCmp('recorddate_from').getValue();
	    				   var end=Ext.getCmp('recorddate_to').getValue();
	    				   var picker=Ext.getCmp('picker');
	    				   console.log(picker);
	    				   console.log(picker.numDays);
	    				   console.log(picker.cells);
	    				   console.log(picker.dom);	
	    				   console.log(document.getElementById('picker'));    				   
	    				   for(var i=0; i < picker.numDays; ++i) {
	    					   var cell=picker.cells.elements[i];
	    					   var value=cell.firstChild.dateValue;
	    					   if(end!=null&&value >= start.getTime()&&value <= end.getTime()) {
	    						   cell.className = picker.baseCls + '-today';
	    					   }else{
	    						   cell.className= picker.baseCls + '-disabled';
	    					   }
	    				   }
	    			   }   	
	    		   }, 
	    		   'datefield[name=recorddate_to]':{
	    			   change:function(field){
	    				   var start=Ext.getCmp('recorddate_from').getValue();
	    				   var end=Ext.getCmp('recorddate_to').getValue();
	    				   var picker=Ext.getCmp('picker');
	    				   for(var i=0; i < picker.numDays; ++i) {
	    					   var cell=picker.cells.elements[i];
	    					   var value=cell.firstChild.dateValue;
	    					   if(start!=null&&value >= start.getTime()&&value <= end.getTime()) {
	    						   cell.className = picker.baseCls + '-today';
	    					   }else{
	    						   cell.className= picker.baseCls + '-disabled';
	    					   }
	    				   }
	    			   }   	
	    		   }, **/
	    		   'button[id=scan]':{
	    			   click:function(btn){
	    				   var  start=Ext.getCmp('recorddate_from').getValue();
	    				   startdate=start==''?'2012-04-01':start;
	    				   var  end=Ext.getCmp('recorddate_to').getValue();
	    				   enddate=end==''?Ext.Date.format(new Date(),'Y-m-d'):end;
	    				   var testgrid=Ext.getCmp('test');
	    				   var handgrid=Ext.getCmp('hand');
	    				   testgrid.getColumnsAndStore(condition,startdate,enddate);
	    				   handgrid.getColumnsAndStore(condition,startdate,enddate);
	    			   }    		
	    		   },
	    		   'erpAnalyseTestGridPanel': {
	    			   itemclick:  function(selModel, record,el,num){//grid行选择
	    				   if(record.data['testingbug']==0) return;
	    					var emid=record.data['exhibitorid'],
	    					startdate=Ext.getCmp('recorddate_from').getValue(),
	    					enddate=Ext.getCmp('recorddate_to').getValue();   
	    					var data=me.getData(emid,startdate,enddate,'plm/test/getTestData.action');
	    					var store = Ext.create('Ext.data.Store', {
	    						autoLoad:true,
	    						fields:[{name:'bugname',type:'string'},{name:'date',type:'date',format:'Y-m-d'}, {name:'emname',type:'string'}],
	    						data: data
	    					});
	    					if(menu==null){
	    						menu= Ext.create('Ext.menu.Menu', {
	    							async:false, 
	    							id: 'mainMenu',    
	    							ownerCt : this.ownerCt,
	    							renderTo:Ext.getBody(),
	    							width:'540',
	    							style: {
	    								overflow: 'visible', 
	    							},
	    							items: [{
	    								width:540,
	    								xtype:'grid',
	    								columnLines:true,
	    								id:'smallgrid',
	    								height:200,
	    								buttonAlign:'center',
	    								store:store,
	    								bodyStyle: 'background:#EEE5DE; padding:0px;',
	    								columns: [{ header: 'BUG名称',  dataIndex: 'bugname' ,cls :'x-grid-header-1',width:340},
	    								          { header: '日期', dataIndex: 'date', flex: 1 ,cls :'x-grid-header-1',format:'Y-m-d',xtype:'datecolumn'},
	    								          { header: '人员名称', dataIndex: 'emname',cls :'x-grid-header-1' },				         
	    								          ],
	    								          dockedItems: [{ 
	    								        	  buttonAlign:'center',
	    								        	  xtype: 'toolbar',
	    								        	  dock: 'bottom',
	    								        	  style: 'background:#EEE9BF; padding:0px;',
	    								        	  items: [{  xtype: 'button', 
	    								        		  text: '关闭',
	    								        		  iconCls: 'x-button-icon-close',
	    								        		  style :'margin-left:230px',
	    								        		  handler:function(btn){                          
	    								        			  Ext.getCmp('mainMenu').hide();
	    								        		  }
	    								        	  }]
	    								          }], 

	    							}]
	    						});  
	    					}
	    					else Ext.getCmp('smallgrid').getStore().loadData(data);
	    					menu.alignTo(el, 'tl-bl?',[280, 0]);
	    					menu.show();
	    			   }, 
	    		   },
	    		   'erpAnalyseHandGridPanel':{
	    			   itemclick:  function(selModel, record,el,num){//grid行选择
	    				   if(record.data['pendingbug']==0) return;
	    					var emid=record.data['handerid'],
	    					startdate=Ext.getCmp('recorddate_from').getValue(),
	    					enddate=Ext.getCmp('recorddate_to').getValue();  
	    					var data=this.getData(emid,startdate,enddate,'plm/test/getHandData.action');
	    					var store = Ext.create('Ext.data.Store', {
	    						autoLoad:true,
	    						fields:[{name:'bugname',type:'string'},{name:'date',type:'date',format:'Y-m-d'}, {name:'emname',type:'string'}],
	    						data: data
	    					});
	    					if(menu==null){
	    						menu= Ext.create('Ext.menu.Menu', {
	    							async:false, 
	    							id: 'mainMenu',    
	    							ownerCt : this.ownerCt,
	    							renderTo:Ext.getBody(),
	    							width:'540',
	    							style: {
	    								overflow: 'visible', 
	    							},
	    							items: [{
	    								width:540,
	    								xtype:'grid',
	    								columnLines:true,
	    								id:'smallgrid',
	    								height:200,
	    								buttonAlign:'center',
	    								store:store,
	    								bodyStyle: 'background:#EEE5DE; padding:0px;',
	    								columns: [{ header: 'BUG名称',  dataIndex: 'bugname' ,cls :'x-grid-header-1',width:340},
	    								          { header: '日期', dataIndex: 'date', flex: 1 ,cls :'x-grid-header-1',format:'Y-m-d',xtype:'datecolumn'},
	    								          { header: '人员名称', dataIndex: 'emname',cls :'x-grid-header-1' },				         
	    								          ],
	    								          dockedItems: [{ 
	    								        	  buttonAlign:'center',
	    								        	  xtype: 'toolbar',
	    								        	  dock: 'bottom',
	    								        	  style: 'background:#EEE9BF; padding:0px;',
	    								        	  items: [{  xtype: 'button', 
	    								        		  text: '关闭',
	    								        		  iconCls: 'x-button-icon-close',
	    								        		  style :'margin-left:230px',
	    								        		  handler:function(btn){                          
	    								        			  Ext.getCmp('mainMenu').hide();
	    								        		  }
	    								        	  }]
	    								          }], 

	    							}]
	    						});  
	    					}
	    					else Ext.getCmp('smallgrid').getStore().loadData(data);
	    					menu.alignTo(el, 'tl-bl?',[280, 0]);
	    					menu.show();
	    			   }, 
	    		   }
	       });
},	
getData:function(emid,startdate,enddate,url){
	var data=null;
	Ext.Ajax.request({
		url : basePath + url,
		params:{
			startdate:startdate,
			enddate:enddate,
			emid:emid,			
		},
		async:false, 
		method : 'get',
		callback : function(options,success,response){
			var rs = new Ext.decode(response.responseText);
			if(rs.exceptionInfo){
				showError(rs.exceptionInfo);return;
			}
			else if(rs.success){
				data=rs.data;					
			}
		}
	}); 
	return data; 	
}
});