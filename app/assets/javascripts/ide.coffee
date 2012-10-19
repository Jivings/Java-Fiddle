ide = this.ide

ide.getClassname = ()->
  search = ide.getSearchCursor(new RegExp(' *(public)? +(abstract +|final +)?class +([^ ]+)'))
  found = search.findNext()
  return found[3]

