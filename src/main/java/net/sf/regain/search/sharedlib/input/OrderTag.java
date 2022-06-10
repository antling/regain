/*
 * regain - A file search engine providing plenty of formats
 * Copyright (C) 2004  Til Schneider, Thomas Tesche
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: Til Schneider <info@murfman.de>, Thomas Tesche <thtesche@gmail.com>
 */
package net.sf.regain.search.sharedlib.input;

import net.sf.regain.RegainException;
import net.sf.regain.search.SearchToolkit;
import net.sf.regain.search.config.IndexConfig;
import net.sf.regain.search.results.SortingOption;
import net.sf.regain.util.io.Localizer;
import net.sf.regain.util.sharedtag.PageRequest;
import net.sf.regain.util.sharedtag.PageResponse;
import net.sf.regain.util.sharedtag.SharedTag;

/**
 * Generates an select box that contains the current order options.
 * <p>
 * Tag Parameters:
 * <ul>
 * <li><code>size</code>: The size of the input field.</li>
 * </ul>
 *
 * @author Thomas Tesche (thtesche), https://github.com/thtesche
 */
public class OrderTag extends SharedTag {

  /**
   * Called when the parser reaches the end tag.
   *
   * @param request The page request.
   * @param response The page response.
   * @throws RegainException If there was an exception.
   */
  @Override
  public void printEndTag(PageRequest request, PageResponse response)
          throws RegainException {

    // Get the IndexConfig
    IndexConfig[] configArr = SearchToolkit.getIndexConfigArr(request);
    SortingOption[] sortingOptions = null;
    if (configArr.length >= 1) {
      // We take the first index config
      IndexConfig config = configArr[0];
      sortingOptions = config.getSortingOptions();
    }

    if (sortingOptions == null) {
      // create default option relevance
      Localizer localizer = new Localizer(request.getResourceBaseUrl(), "msg", request.getLocale());
      String relevText = localizer.msg("relevance", "relevance");
      sortingOptions = new SortingOption[]{new SortingOption(relevText, "relevance", "desc", 1)};
    }

    if (sortingOptions != null) {
      String order = request.getParameter("order");
      //System.out.println("order: " + order);

      if (order == null || order.length() == 0) {
        order = sortingOptions[0].getFieldNameAndOrder();
        //System.out.println("order set to default: " + order);
      }

      response.print("<select name=\"order\" size=\"1\" >");
      for (int i = 0; i < sortingOptions.length; i++) {

        if (sortingOptions[i].getFieldNameAndOrder().equals(order)) {
          response.print("<option selected value=\"" + sortingOptions[i].getFieldNameAndOrder() + "\">");
        } else {
          response.print("<option value=\"" + sortingOptions[i].getFieldNameAndOrder() + "\">");
        }
        response.print(sortingOptions[i].getDescription());
        response.print("</option>");
      }
      response.print("</select>");

    }
  }
}