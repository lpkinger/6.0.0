Ext.define('erp.view.plm.project.AnalyseForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.AnalyseForm',
	id: 'analyform', 
	layout:'column',
	region: 'north',
    frame : true,
    header: false,//不显示title
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	defaults: { 
    columnWidth:0.5
     },
	items: [{
	    columnWidth:0.65,
	    name:'recorddate',
	    id:'recorddate',
        xtype:'condatefield',
		fieldLabel:'时间区间',
		labelAlign:'right',
		fieldStyle:'background:#EBEBEB',
		labelStyle:'font-size:14px;font-color:#CDC9C9',
    },{
       xtype: 'button',
       text:'查询',
       id:'scan',
       columnWidth:0.1,
       iconCls: 'x-button-icon-scan',
       style:'margin-left:20px;background:#F0F0F0'
    },{
       html:"<div id='sidebar' style='background:#CDCDB4;font-size:16px;font-color:red;text-align:center;'><a  href='" + basePath +"jsps/plm/calendar/NewCalendar.jsp' target='_blank' >查看日历 </a></div>",
       id:'calendar',
       columnWidth:0.2,
      style:'margin-left:40px;'
       
    }],
   /** tbar: [
      '->', {
		name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var grid = Ext.getCmp('querygrid');
    		grid.BaseUtil.exportexcel(grid);
    	}
    }],**/
  /**  buttons: [
    {
      text:'排序',
      enableToggle: true,
      handler:function(btn){
      if(!this.pressed){
       data = Ext.Array.sort(data, function(a, b){
        	return parseFloat(a.percentdone) -parseFloat(b.percentdone);
        });
        store1= Ext.create('Ext.data.Store', {
                 fields:fields,            
                  data:data
               });
          }else {
          data = Ext.Array.sort(data, function(a, b){
        	return parseFloat(b.percentdone)-parseFloat(a.percentdone);
        });
        store1= Ext.create('Ext.data.Store', {
                 fields:fields,            
                  data:data
               });
          
          }
        Ext.getCmp('barchart').bindStore(store1);
        Ext.getCmp('analysegrid').reconfigure(store1,columns);
      }   
    },
    {
        text: '查询',
        handler: function() {
        var condition='';
          var finishdate=Ext.getCmp('finishdate').getValue();
          var prjplan_id=Ext.getCmp('an_prjplanid').getValue();
          if(finishdate!=null&&finishdate!=''&&prjplan_id==''){
         var  date=Ext.util.Format.date(finishdate,'Y-m-d');
           var str="ra_enddate<to_date('" + date + "','YYYY-MM-DD')";
           condition=' where '+str;
          }else if(prjplan_id!=null&&prjplan_id!=''&&finishdate==null){         
          condition=' where ra_prjid='+prjplan_id;
          }else if(prjplan_id!=''&&finishdate!=null){
         var date=Ext.util.Format.date(finishdate,'Y-m-d');
         var str="ra_enddate<to_date('" + date + "','YYYY-MM-DD')";
          condition=' where ra_prjid='+prjplan_id +'AND '+str;
          }else  {
           showError('请设置查询条件！'); return 
          }
          Ext.Ajax.request({//拿到grid的columns
          url : basePath + 'plm/resource/Analysegrid.action',
          async:false, 
          params:{
            condition:condition
          },
          method : 'post',
          callback : function(options,success,response){
          var res = new Ext.decode(response.responseText);
        		if(res.success){
        		data=res.data;
             var  store2= Ext.create('Ext.data.Store', {
                 fields: res.fields,            
                  data:res.data
               });
              Ext.getCmp('barchart').bindStore(store2); 
        Ext.getCmp('analysegrid').reconfigure(store2,columns);
        		}else if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
          }
          }) 
        }
    }, {
        text: '重置',
        handler: function() {
          Ext.getCmp('barchart').bindStore(store);
        Ext.getCmp('analysegrid').reconfigure(store,columns);
            this.up('form').getForm().reset();
        }
    },   
    ],**/
	initComponent : function(){ 
		this.callParent(arguments);
	},
});