Ext.define('erp.view.plm.resource.AnalyseForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.AnalyseForm',
	id: 'analyform', 
	layout:'column',
	frame:true,
	defaults: { 
    columnWidth:1
     },
	items: [{
        fieldLabel: '截止时间',
        xtype:'datefield',
        id:'finishdate',
        name: 'finishdate',
        allowBlank: true
    },{
        fieldLabel: '项目ID',
        xtype:'dbfindtrigger',
        name: 'an_prjplanid',
        id:'an_prjplanid',
        allowBlank: true
    }],
    buttons: [
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
    ],
	initComponent : function(){ 
		this.callParent(arguments);
	},
});