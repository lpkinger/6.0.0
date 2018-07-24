 Ext.define('erp.view.sysmng.upgrade.version.VersionAddPanel',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.VersionAddPanel',
	requires: ['erp.view.core.button.Save'],
	id: 'VersionAddPanel', 
    region: 'north',
    recorddata:null,
   	bodyStyle:{background:'#f1f1f1'},
	height:500,
	width:700,
	//layout:'fit',
	
	border:'0 0 0 0',
    frame : true,
    header: false,//不显示title
	//layout : 'column',
	padding: '0 0 0 0',
	autoScroll : false,	
	buttonAlign :'center',
	buttons:[{
	    	xtype: 'erpSaveButton',
	    	id: 'erpSaveButton',
	    	margin:'5 5 5 5' ,
	    	height: 22,
	    	hidden: false,	    	
	    	handler: function(btn){
	    		
	    		var me=Ext.getCmp('VersionAddPanel');
	    		var id=Ext.getCmp('id').value;	    		
	    		var numid=Ext.getCmp('numid').value;
	    		var version=Ext.getCmp('version').value;
	    		var remark=Ext.getCmp('remark').getValue();
	    		
	    		console.log(Ext.getCmp('numid'));
	    		//var numid=Ext.getCmp('numid')
	    		
	    		if((remark==null||remark=="​"||remark==undefined)
	    				!=((version==null||version=="​"||version==undefined))){
	    				    		    			
	    			Ext.MessageBox.alert("警告框","版本号和升级说明必须同时填写！"); 
	    		}else{
	    			me.save(id,numid,version,remark);	
	    		}

	    		
	    		    		  		
	    	}	    	
	    }],
    items: [
    {
    	
    	xtype:'textfield',
    	fieldLabel: 'id',     
        id: 'id',
       	margin:'20 10 10 10' ,
        allowBlank: true,
        hidden:true
    },
    	{
    	
    	xtype:'textfield',
    	fieldLabel: '标识号',      
        id: 'numid',
       	margin:'20 10 10 10' ,
        allowBlank: true
    },
    {
    	
    	xtype:'numberfield',
    	fieldLabel: '版本号',        
        id: 'version',
        //minValue:5,
        hideTrigger:true,
       	margin:'20 10 10 10' ,
        allowBlank: true
    },
	    {
	    
    	xtype:'htmleditor',
    	fieldLabel: '升级说明',        
        id: 'remark',
        width:650,
        height:300,
       	margin:'20 10 10 10' ,
        allowBlank: false,
        readOnly:false
    }],
    
	initComponent : function(){ 
		this.callParent(arguments);		
	},
	save:function(id,numid,version,remark){
		save={id:id,numid:numid,version:version,remark:remark},
		
	
		
		me.setLoading(true);
		Ext.Ajax.request({
            	//timeout: 5000,
                url : basePath + 'upgrade/saveVersionLog.action',
                params:save,
                callback : function(options,success,response){
                    var res = new Ext.decode(response.responseText);
                    me.setLoading(false);
                    if(res.success){

             			var va=Ext.getCmp('VersionAddPanel');
 	
                    	va.recorddata.set('sn_svnversion',version);
                    	va.recorddata.set('sn_num',numid);
						va.recorddata.commit();

                    	Ext.getCmp('VersionAddPanel').ownerCt.close();
                    	
                    	var vp=Ext.getCmp('versionpanelpanel');
                    	vp.getSysUpgradeLog(numid);
                                        
                    	
                    } else if(res.exceptionInfo){
                        showError(res.exceptionInfo);
                    }
                }
            });
	
	}
	
});