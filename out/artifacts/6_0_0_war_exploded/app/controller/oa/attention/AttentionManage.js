Ext.QuickTips.init();
Ext.define('erp.controller.oa.attention.AttentionManage', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
    		'oa.attention.AttentionManage','oa.attention.AttentionGrid','oa.attention.Form','core.form.ColorField','core.button.Save','core.button.Close',
    		'core.form.ScopeField','oa.attention.AttentionSubGrid','core.trigger.MultiDbfindTrigger','oa.attention.AttentionManageGrid'
    	],
    init:function(){
      var me=this;
    	this.control({ 
    	  'button[id=attention]':{
    	    click:function(){
    	        me.AddAttention();
    	    }
    	  }	,
    	  'erpAttentionManageGridPanel':{
    	   afterrender:function(panel){
    	   }    	    
    	  },
    	  'button[id=accredit]':{
    	    click:function(){
    	     me.accreditAttention();
    	    }
    	  },
    	  'button[id=delete]':{
    	    click:function(){
    	     me.Delete();
    	    }
    	  },
    	  'button[id=rank]':{
    	    click:function(){
    	      me.Rank();
    	    }    	  
    	  }
    	});
    },
    AddAttention:function(){
      var selectedemid=null;
      var condition='';
      if(selectedemid){
        condition="ap_attentedemid="+selectedemid+" AND ap_emid="+emid;
      }else condition= "ap_emid="+0;
       var win = new Ext.window.Window(
				{
					id : 'win',
					height : '80%',
					title:'关注项设置',
					width : '65%',
					maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
					items : [ {
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						html : '<iframe id="iframe_'+ caller+ '" src="'+ basePath+ 'jsps/oa/attention/AttentionSub.jsp?urlcondition='+condition+'&caller=AttentionPerson" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
					} ],

				});
		win.show();
    },
    Rank:function(){
        var win = new Ext.window.Window(
				{
					id : 'win',
					height : '80%',
					title:'等级设置',
					width : '60%',
					maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
					items : [ {
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						html : '<iframe id="iframe_'+ caller+ '" src="'+ basePath+ 'jsps/oa/attention/AttentionGrade.jsp'+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
					} ],

				});
		win.show();   
    },
    accreditAttention:function(){
      var selectedemid=null;
      var condition='';
      if(selectedemid){
        condition="ap_accreditedemid="+selectedemid+" AND aa_emid="+emid;
      }else condition= "aa_emid="+0;
        var win = new Ext.window.Window(
				{
					id : 'win',
					height : '80%',
					title:'授权设置',
					width : '65%',
					maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
					items : [ {
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						html : '<iframe id="iframe_'+ caller+ '" src="'+ basePath+ 'jsps/oa/attention/AttentionSub.jsp?urlcondition='+condition+'&caller=AccreditAttention" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
					} ],

				});
		win.show();
    
    },
    Delete:function(){
     var grid=Ext.getCmp('AttentionGridPanel');
     var data=grid.getMultiSelected();
     var param=new Object();
     param.data=data;
     Ext.Ajax.request({//拿到form的items
        	url : basePath + 'oa/attention/deleteAttentions.action',
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        	  var res = new Ext.decode(response.responseText);
        	  if(res.success){
        	   saveSuccess(
        	   grid.loadNewStore(grid,{caller:caller,condition: "ap_emid="+emid})
        	   );
        	  }
         }
       });
    }
});