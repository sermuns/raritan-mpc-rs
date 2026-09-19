//! Small XML helpers (attribute lookup + escaping).

/// Naive `name="value"` lookup, enough for the tiny CSC challenge documents.
pub fn xml_attribute(xml: &str, name: &str) -> Option<String> {
    let marker = format!("{name}=\"");
    let start = xml.find(&marker)? + marker.len();
    let end = xml[start..].find('"')? + start;
    Some(xml[start..end].to_owned())
}

/// XML-escapes a value for CSC requests (via quick_xml, the single implementation).
pub fn escape_xml(value: &str) -> String {
    quick_xml::escape::escape(value).into_owned()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn escapes_all_specials() {
        assert_eq!(
            escape_xml("<a href=\"x\">&'"),
            "&lt;a href=&quot;x&quot;&gt;&amp;&apos;"
        );
    }

    #[test]
    fn finds_attribute() {
        assert_eq!(
            xml_attribute(r#"<C ClearText="abc"/>"#, "ClearText").as_deref(),
            Some("abc")
        );
        assert_eq!(xml_attribute("<C/>", "ClearText"), None);
    }
}
