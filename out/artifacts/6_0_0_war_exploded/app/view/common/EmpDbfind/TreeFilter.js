Ext.define('erp.view.common.EmpDbfind.TreeFilter', {
    filterByText: function(text) {
        this.filterBy(text, 'text');
    },
    filterBy: function(text, by) {
        if(!Ext.isEmpty(text)) {
        	var view = this.getView(),
	            me = this,
	            nodesAndParents = [];
	        this.getRootNode().cascadeBy(function(tree, view){
	            var currNode = this;
	            if(currNode && (currNode.raw && currNode.data.leaf &&  
	            		currNode.raw.data[by] && currNode.raw.data[by].toString().toLowerCase().indexOf(text.toLowerCase()) > -1)) {
	            	if(currNode.parentNode) {
	            		try {
	            			me.expandPath(currNode.getPath());
	            		} catch (e) {
	            			
	            		}
	            	}
	                while(currNode.parentNode) {
	                    nodesAndParents.push(currNode.id);
	                    currNode = currNode.parentNode;
	                }
	            }
	        }, null, [me, view]);
	        this.getRootNode().cascadeBy(function(tree, view){
	            var uiNode = view.getNodeByRecord(this);
	            if(uiNode && !Ext.Array.contains(nodesAndParents, this.id)) {
	                Ext.get(uiNode).setDisplayed('none');
	            }
	        }, null, [me, view]);
        }
    },
    clearFilter: function() {
        var view = this.getView();
        this.getRootNode().cascadeBy(function(tree, view){
            var uiNode = view.getNodeByRecord(this);
            if(uiNode) {
                Ext.get(uiNode).setDisplayed('table-row');
            }
        }, null, [this, view]);
    }
});