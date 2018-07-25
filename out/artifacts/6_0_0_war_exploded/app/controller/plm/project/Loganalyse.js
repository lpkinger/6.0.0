Ext.QuickTips.init();
Ext.define('erp.controller.plm.project.Loganalyse', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.project.Loganalyse','plm.project.ProjectTreePanel','common.datalist.Toolbar','plm.project.LogGrid','core.form.ConDateField',
    		'plm.project.AnalyseForm'
    	],
    init:function(){
         var menu =null;
    	this.control({ 
    	    'erpProjectTreePanel': {
    			itemmousedown: function(selModel, record){
    				if(record.get('leaf')){	
    				var id=record.get('id');
    				condition=" wr_prjid='"+id+"'";
    				Ext.getCmp('grid').getColumnsAndStore(condition);
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
    		   Ext.getCmp('recorddate_from').setValue('2012-12-24');
    		}
    		},
    		/*'datefield[name=recorddate_from]':{
    		 change:function(field){
    		    var start=Ext.getCmp('recorddate_from').getValue();
    		    var end=Ext.getCmp('recorddate_to').getValue();
    		    var picker=Ext.getCmp('picker');
    		    for(var i=0; i < picker.numDays; ++i) {
    		    var cell=picker.cells.elements[i];
                var value=cell.firstChild.dateValue;
                if(end!=null&&value >= start.getTime()&&value < end.getTime()) {
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
                if(start!=null&&value >= start.getTime()&&value < end.getTime()) {
                cell.className = picker.baseCls + '-today';
                }else{
                cell.className= picker.baseCls + '-disabled';
                }
    		    }
    		  }   	
    		}, */
    		'button[id=scan]':{
    		  click:function(btn){
    		   var  start=Ext.getCmp('recorddate_from').getValue();
    		    startdate=start==''?'2012-12-24':start;
    		   var  end=Ext.getCmp('recorddate_to').getValue();
    		    enddate=end==''?Ext.Date.format(new Date(),'Y-m-d'):end;
    		    Ext.getCmp('grid').getColumnsAndStore(condition,startdate,enddate);
    		  }    		
    		},
    	    'erpLogGridPanel': {
    		  itemclick:  function(selModel, record,el,num){//grid行选择
    		  if(record.data['dvalue']==0) return;
    		  var emid=record.data['id'],
    		      startdate=record.data['startdate'],
    		      enddate=record.data['enddate'],
    		      recorder=record.data['name'];
    		  var data=this.getData(emid,recorder,startdate,enddate);
    		  var store = Ext.create('Ext.data.Store', {
    		            autoLoad:true,
            		    fields:[{name:'name',type:'string'},{name:'date',type:'date',format:'Y-m-d'}, {name:'type',type:'string'}],
            		    data: data
            		});
             if(menu==null){
            menu= Ext.create('Ext.menu.Menu', {
                    async:false, 
                   id: 'mainMenu',    
                   ownerCt : this.ownerCt,
                   renderTo:Ext.getBody(),
		           width:'340',
                   style: {
                    overflow: 'visible', 
                   },
                  items: [{
                  width:340,
                  xtype:'grid',
                  columnLines:true,
                   id:'smallgrid',
                   height:200,
                   buttonAlign:'center',
                   store:store,
                   bodyStyle: 'background:#EEE5DE; padding:0px;',
                   columns: [{ header: '员工名称',  dataIndex: 'name' ,cls :'x-grid-header-1'},
                          { header: '记录日期', dataIndex: 'date', flex: 1 ,cls :'x-grid-header-1',format:'Y-m-d',xtype:'datecolumn'},
                          { header: '备注', dataIndex: 'type',cls :'x-grid-header-1' },
                          // { header: '备注', dataIndex: 'remark',cls :'x-grid-header-1' }
                          ],
                   dockedItems: [{ 
                               buttonAlign:'center',
                               xtype: 'toolbar',
                               dock: 'bottom',
                               style: 'background:#EEE9BF; padding:0px;',
                               items: [{  xtype: 'button', 
                                      text: '关闭',
                                      iconCls: 'x-button-icon-close',
                                      style :'margin-left:130px',
                                      handler:function(btn){                          
                                        Ext.getCmp('mainMenu').hide();
                                       }
                                   }]
                              }], 
                               
                    }]
          });  
          }
           Ext.getCmp('smallgrid').getStore().loadData(data);
          menu.alignTo(el, 'tl-bl?',[280, 0]);
    	  menu.show();
      }, 
         itemdblclick:function(){
    
         }
      }
    	});
    },	
      getData:function(emid,recorder,startdate,enddate){
        var data=null;
         Ext.Ajax.request({
		    url : basePath + "plm/log/Smallgrid.action",
		    params:{
		        startdate:startdate,
		        enddate:enddate,
				emid:emid,
				recorder:recorder
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