Ext.define('erp.view.sys.hr.SimpleJprocess',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.simplejprocess',
	id:'simplejprocess',
	/*style: {
	    background:'white',
	},*/
	bodyStyle : 'background:white;',
	layout:'border',
	items: [{
			region: 'north',
			width: '10%',
			xtype: 'simplejpform'
	},/*{
		region: 'south',
		width: '90%',
		xtype: 'simplejprocesspanel'
	},*/
	{
		xtype:'panel',
		region:'center',
		width:'80%',
		/*height:1000,*/
		layout:'border',
		bodyStyle : 'background:white;',
		border: false,
		autoScroll:true,
		items:[
		       {
		          region: 'west',
		    	  width: '70%',
		    	  height:'100%',
		    	  xtype: 'simplejprocesspanel'   
		       },
		       {
		    	  region:'center',
		    	  xtype:'form' ,
		    	  width:'10%',
		    	  height:100,
		    	  /*id:'jpescriptionform',*/
		    	  border:false,
		    	  bodyPadding: 10,
		    	  items: [{
		    	        xtype     : 'panel',
		    	        name      : 'message',
		    	        anchor    : '95%',
		    	        bodyStyle:'padding-left:5px;',
		    	        width :210,
		    	        height:410,
		    	        html:'<br/><span style="color:#0092cf;"><b>&nbsp;&nbsp;流程操作说明:</b></span><br/><br/><span style="color:black;">&nbsp;&nbsp;1.&nbsp审批流设置分为简化设置和高级设置;简化设置的流程只支持串行的流程，高级设置支持分支流程和并行流程，高级设置同时支持审批节点中设置审批要点、知会人、审批限办时间等。</br></br>&nbsp;&nbsp;2.&nbsp;简化设置中，我们默认第一个节点为流程发起人，最后一个节点为流程结束人员(流程图中至少存在两个节点)。</br></br>&nbsp;&nbsp;3.&nbsp;简化设置中，"+"代表新增子节点，"×"代表删除当前节点。</br></br>&nbsp;&nbsp;4.&nbsp;简化设置中，节点支持按人，岗位，角色设置(不支持两种不同类型组合设置)。</span>'
		    	    }]
		       }]
		}
	],
	initComponent : function(){
		this.callParent(arguments);
	}
});